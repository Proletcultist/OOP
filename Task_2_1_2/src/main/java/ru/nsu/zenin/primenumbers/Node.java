package ru.nsu.zenin.primenumbers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import ru.nsu.zenin.primenumbers.protocol.*;
import ru.nsu.zenin.primenumbers.protocol.exception.*;

public class Node {
    private static final InetAddress MULTICAST_GROUP;
    private static final int UDP_PORT = 9090;
    private static final int HEARTBEAT_INTERVAL_MS = 2000;
    private static final int PEER_TIMEOUT_MS = 6000;
    private static final int DISCOVERY_TIME = 1000;

    private final InetAddress ip;
    private final int tcpPort;

    private MulticastSocket multicastSock;
    private AsynchronousServerSocketChannel tcpServer;
    private AsynchronousChannelGroup channelGroup;

    private ThreadPoolExecutor computationPool;
    private final ScheduledExecutorService heartbeatScheduler = Executors.newScheduledThreadPool(1);

    private final Map<UUID, AtomicBoolean> activeTasks =
            new ConcurrentHashMap<UUID, AtomicBoolean>();
    private final Set<Connection> peers = ConcurrentHashMap.newKeySet();
    private final Set<InetAddress> peerIps = ConcurrentHashMap.newKeySet();

    private UUID currentTaskUUID = null;
    private CompletableFuture<Boolean> currentTaskFuture;
    private final AtomicInteger activeSubtasks = new AtomicInteger(0);

    static {
        try {
            MULTICAST_GROUP = InetAddress.getByName("224.0.0.70");
        } catch (UnknownHostException e) {
            throw new RuntimeException();
        }
    }

    public Node(InetAddress ip, int tcpPort) throws IOException, UnknownHostException {
        this.ip = ip;
        this.tcpPort = tcpPort;
        this.computationPool =
                (ThreadPoolExecutor)
                        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        this.channelGroup =
                AsynchronousChannelGroup.withFixedThreadPool(4, Executors.defaultThreadFactory());
        this.tcpServer =
                AsynchronousServerSocketChannel.open(channelGroup)
                        .bind(new InetSocketAddress(ip, tcpPort));
        this.multicastSock = new MulticastSocket(UDP_PORT);
        this.multicastSock.joinGroup(MULTICAST_GROUP);
    }

    public void start() {
        startTcpServer();
        startUdpListener();
        startHeartbeatChecker();
    }

    public void shutdown() {}

    public void setWorkerPoolSize(int size) {
        computationPool.setCorePoolSize(size);
        computationPool.setMaximumPoolSize(size);
    }

    public boolean submitTask(int[] array) throws Exception {
        currentTaskUUID = UUID.randomUUID();
        currentTaskFuture = new CompletableFuture<Boolean>();

        discoverNodes();

        Thread.sleep(DISCOVERY_TIME);

        List<Connection> availablePeers = new ArrayList<>(peers);
        // Fail if no active peers found
        if (availablePeers.isEmpty()) {
            currentTaskUUID = null;
            currentTaskFuture = null;
            throw new IllegalStateException("No available worker nodes found in the LAN");
        }

        activeSubtasks.set(0);

        int chunkSize = (int) Math.ceil((double) array.length / availablePeers.size());

        for (int i = 0; i < availablePeers.size(); i++) {
            int start = i * chunkSize;
            int length = Math.min(chunkSize, array.length - start);
            if (length <= 0) break;

            int[] chunk = new int[length];
            System.arraycopy(array, start, chunk, 0, length);

            availablePeers.get(i).getSubmittedTasks().add(new Task(currentTaskUUID, chunk));
            sendMessage(availablePeers.get(i), new Message.TaskSubmit(currentTaskUUID, chunk));

            activeSubtasks.getAndIncrement();
        }

        try {
            boolean result = currentTaskFuture.get();
            return result;
        } finally {
            currentTaskUUID = null;
            currentTaskFuture = null;
        }
    }

    private void startTcpServer() {
        tcpServer.accept(
                null,
                new CompletionHandler<AsynchronousSocketChannel, Void>() {
                    @Override
                    public void completed(AsynchronousSocketChannel client, Void attachment) {
                        tcpServer.accept(
                                null, (CompletionHandler<AsynchronousSocketChannel, Void>) this);
                        try {
                            InetAddress address =
                                    ((InetSocketAddress) client.getRemoteAddress()).getAddress();
                            if (peerIps.contains(address)) {
                                client.close();
                            } else {
                                Connection newConn =
                                        new Connection(address, client, System.currentTimeMillis());
                                peers.add(newConn);
                                peerIps.add(address);
                                listenToPeer(newConn);
                            }
                        } catch (IOException ignore) {
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {}
                });
    }

    private void listenToPeer(Connection peer) {
        class ReadContext {
            final ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
            ByteBuffer bodyBuffer = null;
            boolean readingLength = true;
        }

        ReadContext context = new ReadContext();

        peer.getChannel()
                .read(
                        context.lengthBuffer,
                        context,
                        new CompletionHandler<Integer, ReadContext>() {
                            @Override
                            public void completed(Integer bytesRead, ReadContext ctx) {
                                if (bytesRead == -1) {
                                    disconnect(peer);
                                    return;
                                }

                                if (ctx.readingLength) {
                                    if (ctx.lengthBuffer.hasRemaining()) {
                                        peer.getChannel().read(ctx.lengthBuffer, ctx, this);
                                        return;
                                    }

                                    ctx.lengthBuffer.flip();
                                    int messageLength = ctx.lengthBuffer.getInt();
                                    ctx.lengthBuffer.clear();

                                    ctx.bodyBuffer = ByteBuffer.allocate(messageLength);
                                    ctx.readingLength = false;

                                    peer.getChannel().read(ctx.bodyBuffer, ctx, this);

                                } else {
                                    if (ctx.bodyBuffer.hasRemaining()) {
                                        peer.getChannel().read(ctx.bodyBuffer, ctx, this);
                                        return;
                                    }

                                    ctx.bodyBuffer.flip();
                                    byte[] frameData = new byte[ctx.bodyBuffer.remaining()];
                                    ctx.bodyBuffer.get(frameData);

                                    peer.setLastSeen(System.currentTimeMillis());

                                    try {
                                        handleTcpMessage(peer, Codec.deserialize(frameData));
                                    } catch (Exception e) {
                                        System.err.println(
                                                "Error processing frame, disconnecting peer: "
                                                        + e.getMessage());
                                        disconnect(peer);
                                        return;
                                    }

                                    ctx.bodyBuffer = null;
                                    ctx.readingLength = true;

                                    peer.getChannel().read(ctx.lengthBuffer, ctx, this);
                                }
                            }

                            @Override
                            public void failed(Throwable exc, ReadContext ctx) {
                                disconnect(peer);
                            }
                        });
    }

    private void handleTcpMessage(Connection peer, Message msg) {
        switch (msg) {
            case Message.Ping p -> sendMessage(peer, new Message.Pong());
            case Message.Pong p -> peer.setLastSeen(System.currentTimeMillis());
            case Message.TaskSubmit t -> {
                AtomicBoolean isCancelled = new AtomicBoolean(false);
                activeTasks.put(t.taskId(), isCancelled);

                CompletableFuture.supplyAsync(
                                () -> computeChunk(t.numbers(), isCancelled), computationPool)
                        .whenComplete(
                                (result, exception) -> {
                                    activeTasks.remove(t.taskId());
                                    if (!isCancelled.get()) {
                                        sendMessage(
                                                peer, new Message.TaskResult(t.taskId(), result));
                                    }
                                });
            }
            case Message.TaskStop s -> {
                AtomicBoolean isCancelled = activeTasks.get(s.taskId());
                if (isCancelled != null) {
                    isCancelled.set(true);
                }
            }
            case Message.TaskResult r -> {
                if (currentTaskFuture != null
                        && currentTaskUUID.equals(r.taskId())
                        && !currentTaskFuture.isDone()) {
                    if (r.hasComposite()) {
                        broadcast(new Message.TaskStop(r.taskId()));
                        currentTaskFuture.complete(true);
                    } else {
                        if (activeSubtasks.decrementAndGet() == 0) {
                            currentTaskFuture.complete(false);
                        }
                    }
                }
            }
            default -> {}
        }
    }

    private boolean computeChunk(int[] numbers, AtomicBoolean isCancelled) {
        for (int num : numbers) {
            if (isCancelled.get()) {
                return false;
            }

            if (num <= 1) continue;
            for (int i = 2; i * i <= num; i++) {
                if (num % i == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void startUdpListener() {
        new Thread(
                        () -> {
                            try {
                                byte[] buffer = new byte[1024];
                                while (true) {
                                    DatagramPacket packet =
                                            new DatagramPacket(buffer, buffer.length);
                                    multicastSock.receive(packet);

                                    byte[] data =
                                            Arrays.copyOf(packet.getData(), packet.getLength());
                                    Message msg = Codec.deserialize(data);

                                    if (msg instanceof Message.Discover) {
                                        // Reply with PRESENCE message with unicast dst
                                        byte[] reply =
                                                Codec.serialize(
                                                        ProtocolVersion.LEET_VER,
                                                        new Message.Presence(this.tcpPort));
                                        multicastSock.send(
                                                new DatagramPacket(
                                                        reply,
                                                        reply.length,
                                                        packet.getSocketAddress()));
                                    } else if (msg instanceof Message.Presence p) {
                                        // Connect to peer with TCP
                                        connectToPeer(packet.getAddress(), p.tcpPort());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        })
                .start();
    }

    private void discoverNodes() throws IOException {
        byte[] payload = Codec.serialize(ProtocolVersion.LEET_VER, new Message.Discover());
        DatagramPacket packet =
                new DatagramPacket(payload, payload.length, MULTICAST_GROUP, UDP_PORT);
        multicastSock.send(packet);
    }

    private void connectToPeer(InetAddress address, int port) {
        if (peerIps.contains(address)) {
            return;
        }

        try {
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open(channelGroup);
            client.connect(
                    new InetSocketAddress(address, port),
                    null,
                    new CompletionHandler<Void, Void>() {
                        @Override
                        public void completed(Void result, Void attachment) {
                            Connection newConn =
                                    new Connection(address, client, System.currentTimeMillis());
                            peers.add(newConn);
                            peerIps.add(address);
                            listenToPeer(newConn);
                        }

                        @Override
                        public void failed(Throwable exc, Void attachment) {}
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startHeartbeatChecker() {
        heartbeatScheduler.scheduleAtFixedRate(
                () -> {
                    long now = System.currentTimeMillis();
                    for (Connection peer : peers) {
                        if (now - peer.getLastSeen() > PEER_TIMEOUT_MS) {
                            disconnect(peer); // Drop inactive node
                        } else if (now - peer.getLastSeen() > HEARTBEAT_INTERVAL_MS) {
                            sendMessage(peer, new Message.Ping());
                        }
                    }
                },
                HEARTBEAT_INTERVAL_MS,
                HEARTBEAT_INTERVAL_MS,
                TimeUnit.MILLISECONDS);
    }

    private void sendMessage(Connection peer, Message msg) {
        byte[] payload = Codec.serialize(ProtocolVersion.LEET_VER, msg);
        ByteBuffer buffer = ByteBuffer.wrap(payload);

        peer.getWriteQueue().add(buffer);
        writeNext(peer);
    }

    private void writeNext(Connection peer) {
        if (!peer.getIsWriting().compareAndSet(false, true)) {
            // A write is already in progress
            return;
        }

        ByteBuffer nextBuffer = peer.getWriteQueue().peek();
        if (nextBuffer == null) {
            peer.getIsWriting().set(false);

            if (!peer.getWriteQueue().isEmpty() && peer.getIsWriting().compareAndSet(false, true)) {
                nextBuffer = peer.getWriteQueue().peek();
            } else {
                return;
            }
        }

        final ByteBuffer bufferToWrite = nextBuffer;

        peer.getChannel()
                .write(
                        bufferToWrite,
                        peer,
                        new CompletionHandler<Integer, Connection>() {
                            @Override
                            public void completed(Integer bytesWritten, Connection p) {
                                if (bufferToWrite.hasRemaining()) {
                                    p.getIsWriting().set(false);
                                    writeNext(p);
                                } else {
                                    p.getWriteQueue().poll();

                                    p.getIsWriting().set(false);
                                    writeNext(p);
                                }
                            }

                            @Override
                            public void failed(Throwable exc, Connection p) {
                                System.err.println("Async write failed: " + exc.getMessage());
                                p.getIsWriting().set(false);
                                disconnect(p);
                            }
                        });
    }

    private void broadcast(Message msg) {
        for (Connection peer : peers) {
            sendMessage(peer, msg);
        }
    }

    private void disconnect(Connection peer) {
        peerIps.remove(peer.getAddress());
        peers.remove(peer);
        try {
            peer.getChannel().close();
        } catch (IOException ignored) {
        }

        // TODO Resubmit task instead
        if (currentTaskFuture != null && !currentTaskFuture.isDone()) {
            broadcast(new Message.TaskStop(currentTaskUUID));
            currentTaskFuture.completeExceptionally(new RuntimeException("Worker failed"));
        }
        /*
        if (!peer.getSubmittedTasks().isEmpty()) {
            discoverNodes();

            Thread.sleep(DISCOVERY_TIME);

            List<Connection> availablePeers = new ArrayList<>(peers);

            // Fail if no active peers found
            if (availablePeers.isEmpty()) {
                currentTaskFuture.completeExceptionally(new IllegalStateException("No available worker nodes found in the LAN"));
                currentTaskUUID = null;
                currentTaskFuture = null;
            }

            int i = 0;
            for (Task task : peer.getSubmittedTasks()) {

            }
        }
        */
    }
}
