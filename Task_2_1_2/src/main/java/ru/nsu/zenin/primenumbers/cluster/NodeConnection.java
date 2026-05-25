package ru.nsu.zenin.primenumbers.cluster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import ru.nsu.zenin.primenumbers.cluster.protocol.Codec;
import ru.nsu.zenin.primenumbers.cluster.protocol.Message;
import ru.nsu.zenin.primenumbers.cluster.protocol.ProtocolVersion;

public abstract class NodeConnection implements AutoCloseable {
    private static final TimeUnit HEARTBEAT_UNIT = TimeUnit.SECONDS;
    private static final long HEARTBEAT_RATE = 2;

    private final ProtocolVersion ver;

    @Getter private final UUID localNodeId;
    @Getter private UUID remoteNodeId = null;

    private final AtomicReference<State> state;

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private final Thread readingThread;
    private final Thread writingThread;
    private final Thread heartbeatThread;

    private CompletableFuture<?> pingedFuture;

    private long lastSeen;
    // private final Set<CompletableFuture<Boolean>> tasks = new
    // HashSet<CompletableFuture<Boolean>>();
    private final BlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<byte[]>();

    public NodeConnection(ProtocolVersion ver, Socket socket, UUID localNodeId) throws IOException {
        this.ver = ver;
        this.localNodeId = localNodeId;
        this.socket = socket;

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        lastSeen = System.currentTimeMillis();

        state = new AtomicReference(State.CONNECTED);
        pingedFuture = CompletableFuture.completedFuture(null);

        try {
            send(new Message.Handshake(localNodeId));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        readingThread = Thread.ofVirtual().start(() -> serviceReceivings());
        writingThread = Thread.ofVirtual().start(() -> serviceSendings());
        heartbeatThread = Thread.ofVirtual().start(() -> heartbeat());
    }

    protected abstract void onStateChange(State state);

    protected abstract void onIncomingTask(int[] nums, CompletableFuture<Boolean> future);

    // public CompletableFuture<Boolean> submit(int[] numbers) {}

    private void serviceReceivings() {
        try {
            while (!Thread.interrupted()) {
                Message msg = recieve();
                switch (msg) {
                    case Message.Presence p -> {}
                    case Message.TaskSubmit t -> {}
                    case Message.TaskResult r -> {}
                    case Message.TaskStop s -> {}
                    case Message.Ping p -> send(new Message.Pong());
                    case Message.Pong pp -> pingedFuture.complete(null);
                    case Message.Handshake h -> {
                        if (state.getAndSet(State.IDENTIFIED) == State.CONNECTED) {
                            remoteNodeId = h.nodeId();
                            onStateChange(State.IDENTIFIED);
                        } else {
                            tryClose();
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            tryClose();
        }
    }

    private void serviceSendings() {
        try {
            while (!Thread.interrupted()) {
                byte[] data = sendQueue.take();
                out.write(data, 0, data.length);
            }
        } catch (Exception e) {
            tryClose();
        }
    }

    private void heartbeat() {
        try {
            HEARTBEAT_UNIT.sleep(HEARTBEAT_RATE);

            while (!Thread.interrupted()) {
                pingedFuture = new CompletableFuture<>();

                send(new Message.Ping());

                pingedFuture.get(HEARTBEAT_RATE, HEARTBEAT_UNIT);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            tryClose();
        }
    }

    public void tryClose() {
        try {
            close();
        } catch (IllegalStateException ignore) {
        }
    }

    public void close() {
        if (state.getAndSet(State.DISCONNECTED) != State.DISCONNECTED) {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException ignore) {
            }

            if (readingThread != null) {
                readingThread.interrupt();
            }
            if (writingThread != null) {
                writingThread.interrupt();
            }
            if (heartbeatThread != null) {
                heartbeatThread.interrupt();
            }

            // TODO: Fail all tasks

            onStateChange(State.DISCONNECTED);
        } else {
            throw new IllegalStateException("Connection already closed");
        }
    }

    private Message recieve() throws Exception {
        return Codec.deserialize(in);
    }

    private void send(Message msg) throws InterruptedException, IOException {
        sendQueue.put(Codec.serialize(ver, msg));
    }

    public enum State {
        CONNECTED,
        IDENTIFIED,
        DISCONNECTED
    }
}
