package ru.nsu.zenin.primenumbers.cluster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import ru.nsu.zenin.primenumbers.cluster.protocol.Codec;
import ru.nsu.zenin.primenumbers.cluster.protocol.Message;
import ru.nsu.zenin.primenumbers.cluster.protocol.ProtocolVersion;

public abstract class NodeConnection implements AutoCloseable {
    private final ProtocolVersion ver;

    @Getter private final UUID localNodeId;
    @Getter private UUID remoteNodeId = null;

    private final AtomicReference<State> state;

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private final Thread readingThread;
    private final Thread writingThread;

    private long lastSeen;
    private Set<CompletableFuture<Boolean>> tasks = new HashSet<CompletableFuture<Boolean>>();
    private final BlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<byte[]>();

    public NodeConnection(ProtocolVersion ver, Socket socket, UUID localNodeId) throws IOException {
        this.ver = ver;
        this.localNodeId = localNodeId;
        this.socket = socket;

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        lastSeen = System.currentTimeMillis();

        state = new AtomicReference(State.CONNECTED);

        readingThread = Thread.ofVirtual().start(() -> serviceReceivings());
        writingThread = Thread.ofVirtual().start(() -> serviceSendings());
        // TODO: Don't forget to be state-aware in timeout thread
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
                    case Message.Ping p -> {}
                    case Message.Pong pp -> {}
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
        } catch (Exception e) {
            tryClose();
        }
    }

    private void serviceSendings() {
        try {
            byte[] handshake = Codec.serialize(ver, new Message.Handshake(localNodeId));
            out.write(handshake, 0, handshake.length);

            while (!Thread.interrupted()) {
                byte[] data = sendQueue.take();
                out.write(data, 0, data.length);
            }
        } catch (Exception e) {
            tryClose();
        }
    }

    private Message recieve() throws Exception {
        return Codec.deserialize(in);
    }

    private void send(Message msg) throws InterruptedException, IOException {
        sendQueue.put(Codec.serialize(ver, msg));
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

            readingThread.interrupt();
            writingThread.interrupt();

            onStateChange(State.DISCONNECTED);
        } else {
            throw new IllegalStateException("Connection already closed");
        }
    }

    public enum State {
        CONNECTED,
        IDENTIFIED,
        DISCONNECTED
    }
}
