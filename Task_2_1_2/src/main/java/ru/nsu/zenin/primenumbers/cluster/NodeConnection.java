package ru.nsu.zenin.primenumbers.cluster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import ru.nsu.zenin.primenumbers.cluster.exception.NodeFaultException;
import ru.nsu.zenin.primenumbers.cluster.protocol.Codec;
import ru.nsu.zenin.primenumbers.cluster.protocol.Message;
import ru.nsu.zenin.primenumbers.cluster.protocol.ProtocolVersion;

public abstract class NodeConnection {
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

    private volatile CompletableFuture<?> pingedFuture;

    private final Map<UUID, CompletableFuture<Boolean>> submittedTasks =
            new ConcurrentHashMap<UUID, CompletableFuture<Boolean>>();
    private final Map<UUID, CompletableFuture<Boolean>> receivedTasks =
            new ConcurrentHashMap<UUID, CompletableFuture<Boolean>>();
    private final BlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<byte[]>();

    public NodeConnection(ProtocolVersion ver, Socket socket, UUID localNodeId)
            throws IOException, InterruptedException {
        this.ver = ver;
        this.localNodeId = localNodeId;
        this.socket = socket;

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        send(new Message.Handshake(localNodeId));

        pingedFuture = CompletableFuture.completedFuture(null);

        state = new AtomicReference(State.CONNECTED);

        readingThread = Thread.ofVirtual().start(() -> serviceReceivings());
        writingThread = Thread.ofVirtual().start(() -> serviceSendings());
        heartbeatThread = Thread.ofVirtual().start(() -> heartbeat());
    }

    protected abstract void onStateChange(State state);

    protected abstract void onIncomingTask(int[] nums, CompletableFuture<Boolean> future);

    public CompletableFuture<Boolean> submit(int[] numbers) {
        UUID taskId = UUID.randomUUID();
        CompletableFuture<Boolean> fut = new CompletableFuture<Boolean>();
        fut.whenComplete(
                (result, exception) -> {
                    if (fut.isCancelled()) {
                        try {
                            send(new Message.TaskStop(taskId));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } catch (IOException e) {
                            close();
                        }
                    }
                });
        submittedTasks.put(taskId, fut);

        // If connection isn't a thing - fail task
        if (state.get() == State.DISCONNECTED) {
            submittedTasks.remove(taskId);
            fut.completeExceptionally(new IOException("Connection is down"));
        } else {
            try {
                send(new Message.TaskSubmit(taskId, numbers));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                close();
            }
        }

        return fut;
    }

    private void serviceReceivings() {
        try {
            Message fst_msg = recieve();
            if (fst_msg instanceof Message.Handshake handshake) {
                if (state.getAndSet(State.IDENTIFIED) == State.CONNECTED) {
                    remoteNodeId = handshake.nodeId();
                    onStateChange(State.IDENTIFIED);
                } else {
                    close();
                }
            } else {
                close();
                return;
            }

            while (!Thread.interrupted()) {
                Message msg = recieve();
                switch (msg) {
                    case Message.Presence p -> {}
                    case Message.TaskSubmit t -> handleTaskSubmit(t);
                    case Message.TaskResult r -> {
                        CompletableFuture<Boolean> fut = submittedTasks.remove(r.taskId());
                        if (fut != null) {
                            fut.complete(r.hasComposite());
                        }
                    }
                    case Message.TaskStop s -> {
                        CompletableFuture<Boolean> fut = receivedTasks.remove(s.taskId());
                        if (fut != null) {
                            fut.cancel(true);
                        }
                    }
                    case Message.TaskFailed f -> {
                        CompletableFuture<Boolean> fut = submittedTasks.remove(f.taskId());
                        if (fut != null) {
                            fut.completeExceptionally(new NodeFaultException("Node fault"));
                        }
                    }
                    case Message.Ping p -> send(new Message.Pong());
                    case Message.Pong pp -> pingedFuture.complete(null);
                    case Message.Handshake h -> {}
                }
            }
        } catch (InterruptedException e) {
            return;
        } catch (Exception e) {
            close();
        }
    }

    private void handleTaskSubmit(Message.TaskSubmit t) {
        CompletableFuture<Boolean> fut = new CompletableFuture<Boolean>();
        fut.whenComplete(
                (result, exception) -> {
                    try {
                        if (exception != null) {
                            send(new Message.TaskFailed(t.taskId()));
                        } else {
                            send(new Message.TaskResult(t.taskId(), result));
                        }
                        receivedTasks.remove(t.taskId());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (IOException e) {
                        close();
                    }
                });
        receivedTasks.put(t.taskId(), fut);

        onIncomingTask(t.numbers(), fut);
    }

    private void serviceSendings() {
        try {
            while (!Thread.interrupted()) {
                byte[] data = sendQueue.take();
                out.write(data, 0, data.length);
            }
        } catch (InterruptedException e) {
            return;
        } catch (Exception e) {
            close();
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
            return;
        } catch (Exception e) {
            close();
        }
    }

    public boolean close() {
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

            for (CompletableFuture<Boolean> fut : submittedTasks.values()) {
                fut.completeExceptionally(new IOException("Connection is down"));
            }
            for (CompletableFuture<Boolean> fut : receivedTasks.values()) {
                fut.cancel(true);
            }

            onStateChange(State.DISCONNECTED);

            return true;
        } else {
            return false;
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
