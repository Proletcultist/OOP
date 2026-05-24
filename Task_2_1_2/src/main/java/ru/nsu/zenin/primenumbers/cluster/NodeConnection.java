package ru.nsu.zenin.primenumbers.cluster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import ru.nsu.zenin.primenumbers.cluster.protocol.Codec;
import ru.nsu.zenin.primenumbers.cluster.protocol.Message;
import ru.nsu.zenin.primenumbers.cluster.protocol.ProtocolVersion;

public abstract class NodeConnection implements AutoCloseable {
    private final ProtocolVersion ver;

    private final AtomicBoolean isOpen;

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private final Thread readingThread;
    private final Thread writingThread;

    private long lastSeen;
    private Set<CompletableFuture<Boolean>> tasks = new HashSet<CompletableFuture<Boolean>>();
    private final BlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<byte[]>();

    public NodeConnection(ProtocolVersion ver, Socket socket) throws IOException {
        this.ver = ver;
        this.socket = socket;

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        lastSeen = System.currentTimeMillis();

        isOpen = new AtomicBoolean(true);

        readingThread = Thread.ofVirtual().start(() -> serviceReceivings());
        writingThread = Thread.ofVirtual().start(() -> serviceSendings());
    }

    protected abstract void onClose();

    protected abstract void onIncomingTask(int[] nums, CompletableFuture<Boolean> future);

    // public CompletableFuture<Boolean> submit(int[] numbers) {}

    private void serviceReceivings() {
        while (true) {
            try {
                Message msg = recieve();
            } catch (Exception e) {
                try {
                    close();
                } catch (IllegalStateException ignore) {
                }
                break;
            }
        }
    }

    private void serviceSendings() {
        while (true) {
            try {
                byte[] data = sendQueue.take();
                out.write(data, 0, data.length);
            } catch (Exception e) {
                try {
                    close();
                } catch (IllegalStateException ignore) {
                }
                break;
            }
        }
    }

    private Message recieve() throws Exception {
        return Codec.deserialize(in);
    }

    private void send(Message msg) throws InterruptedException, IOException {
        sendQueue.put(Codec.serialize(ver, msg));
    }

    public void close() {
        if (isOpen.getAndSet(false)) {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException ignore) {
            }

            readingThread.interrupt();
            writingThread.interrupt();

            onClose();
        } else {
            throw new IllegalStateException("Connection already closed");
        }
    }
}
