package ru.nsu.zenin.primenumbers.cluster;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import ru.nsu.zenin.primenumbers.cluster.protocol.Codec;
import ru.nsu.zenin.primenumbers.cluster.protocol.Message;
import ru.nsu.zenin.primenumbers.cluster.protocol.ProtocolVersion;

public abstract class ClusterConnection implements AutoCloseable {
    private final int CONNECTION_TIMEOUT = 1000;
    private final ProtocolVersion VERSION = ProtocolVersion.LEET_VER;

    private final UUID nodeId;

    private final InetSocketAddress groupAddress;
    private final InetSocketAddress nodeAddress;

    private final Thread udpThread;
    private final Thread tcpThread;

    private final MulticastSocket multicastSock;
    private final ServerSocket tcpServer;

    private final Map<UUID, NodeConnection> nodeConnections;

    public abstract void onIncomingTask(int[] nums, CompletableFuture<Boolean> future);

    public abstract void onClose();

    public ClusterConnection(InetSocketAddress groupAddress, InetSocketAddress nodeAddress)
            throws IOException {
        this.groupAddress = groupAddress;
        this.nodeAddress = nodeAddress;
        this.nodeConnections = new ConcurrentHashMap<UUID, NodeConnection>();
        this.nodeId = UUID.randomUUID();

        this.tcpServer = new ServerSocket();
        tcpServer.bind(nodeAddress);

        multicastSock = new MulticastSocket(groupAddress.getPort());
        NetworkInterface nf = NetworkInterface.getByInetAddress(nodeAddress.getAddress());
        multicastSock.setNetworkInterface(nf);
        multicastSock.joinGroup(groupAddress, nf);

        tcpThread = Thread.ofVirtual().start(() -> serviceTcp());
        udpThread = Thread.ofVirtual().start(() -> serviceUdp());

        try {
            announceNode();
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    public CompletableFuture<Boolean> submit(int[] nums) {
        return submit(nums, ConcurrentHashMap.newKeySet());
    }

    private CompletableFuture<Boolean> submit(int[] nums, Set<UUID> dontSubmitTo) {
        // Make a snapshot of connected nodes
        List<NodeConnection> activeConnections =
                nodeConnections.values().stream()
                        .filter(con -> !dontSubmitTo.contains(con.getRemoteNodeId()))
                        .collect(Collectors.toList());

        if (activeConnections.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalStateException("No nodes in network"));
        }

        int chunkSize = (int) Math.ceil((double) nums.length / activeConnections.size());

        List<CompletableFuture<Boolean>> subfutures = new ArrayList<CompletableFuture<Boolean>>();
        for (int i = 0; i < activeConnections.size(); i++) {
            int start = i * chunkSize;
            int length = Math.min(chunkSize, nums.length - start);
            if (length <= 0) break;

            int[] chunk = Arrays.copyOfRange(nums, start, start + length);

            // Create completable future with retry logic
            CompletableFuture<Boolean> originalFut = activeConnections.get(i).submit(chunk);

            // If original fut was failed - retry, but dont submit to the same node to aboid
            // infinite loops
            final UUID currentNodeUUID = activeConnections.get(i).getRemoteNodeId();
            CompletableFuture<Boolean> retryFut =
                    originalFut.exceptionallyCompose(
                            ex -> {
                                dontSubmitTo.add(currentNodeUUID);
                                return submit(chunk, dontSubmitTo);
                            });

            // If this future with retry logic was cancelled - cancel underlying future
            retryFut.whenComplete(
                    (result, exception) -> {
                        if (retryFut.isCancelled()) {
                            originalFut.cancel(true);
                        }
                    });
            subfutures.add(retryFut);
        }

        CompletableFuture<Boolean> mainFut = new CompletableFuture<Boolean>();
        // Counter for false results
        AtomicInteger falseCounter = new AtomicInteger(0);

        for (CompletableFuture<Boolean> fut : subfutures) {
            fut.whenComplete(
                    (result, exception) -> {
                        if (exception != null) {
                            if (mainFut.completeExceptionally(exception)) {
                                cancelAll(subfutures);
                            }
                        } else {
                            if (result == true) {
                                if (mainFut.complete(result)) {
                                    cancelAll(subfutures);
                                }
                            } else if (falseCounter.incrementAndGet() == subfutures.size()) {
                                mainFut.complete(result);
                            }
                        }
                    });
        }

        return mainFut;
    }

    private static void cancelAll(List<CompletableFuture<Boolean>> futures) {
        for (CompletableFuture<Boolean> fut : futures) {
            fut.cancel(true);
        }
    }

    // TODO: Implement
    @Override
    public void close() {}

    private void serviceTcp() {
        while (!Thread.interrupted()) {
            try {
                Socket socket = tcpServer.accept();
                System.out.println(nodeId + ": Connected new " + socket.getRemoteSocketAddress());
                addNewNodeConnection(socket);
            } catch (IOException ignore) {
            }
        }
    }

    private void serviceUdp() {
        byte[] buffer = new byte[512];
        while (!Thread.interrupted()) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try {
                multicastSock.receive(packet);
            } catch (IOException e) {
                close();
            }

            Message msg;
            try {
                msg = Codec.deserialize(packet.getData(), packet.getOffset(), packet.getLength());
            } catch (Exception ignore) {
                continue;
            }

            if (msg instanceof Message.Presence p
                    && !p.nodeId().equals(nodeId)
                    && !nodeConnections.containsKey(p.nodeId())) {
                System.out.println(nodeId + ": Presence " + p.nodeId());

                try {
                    Socket socket = new Socket();
                    socket.connect(
                            new InetSocketAddress(p.address(), p.port()), CONNECTION_TIMEOUT);
                    addNewNodeConnection(socket);
                } catch (IOException ignore) {
                }
            }
        }
    }

    private void addNewNodeConnection(Socket socket) {
        try {
            NodeConnection newConn =
                    new NodeConnection(VERSION, socket, nodeId) {
                        @Override
                        protected void onStateChange(NodeConnection.State state) {
                            System.out.println(this.getRemoteNodeId() + ": " + state);
                            switch (state) {
                                case NodeConnection.State.CONNECTED -> {}
                                case NodeConnection.State.IDENTIFIED -> {
                                    UUID remote = this.getRemoteNodeId();
                                    if (nodeConnections.containsKey(remote)) {
                                        this.close();
                                    } else {
                                        nodeConnections.put(remote, this);
                                    }
                                }
                                case NodeConnection.State.DISCONNECTED -> {
                                    UUID remote = this.getRemoteNodeId();
                                    if (remote != null) {
                                        nodeConnections.remove(this.getRemoteNodeId());
                                    }
                                }
                            }
                        }

                        @Override
                        protected void onIncomingTask(
                                int[] nums, CompletableFuture<Boolean> future) {
                            ClusterConnection.this.onIncomingTask(nums, future);
                        }
                    };
        } catch (IOException ignore) {
        }
    }

    private void announceNode() throws IOException {
        byte[] data =
                Codec.serialize(
                        VERSION,
                        new Message.Presence(
                                nodeId, nodeAddress.getAddress(), nodeAddress.getPort()));
        DatagramPacket packet = new DatagramPacket(data, data.length, groupAddress);
        multicastSock.send(packet);
    }
}
