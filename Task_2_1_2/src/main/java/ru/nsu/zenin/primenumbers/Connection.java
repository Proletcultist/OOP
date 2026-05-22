package ru.nsu.zenin.primenumbers;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Connection {
    private final InetAddress address;
    private final AsynchronousSocketChannel channel;
    private long lastSeen;
    private final List<Task> submittedTasks = new ArrayList<Task>();
    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<ByteBuffer>();
    private final AtomicBoolean isWriting = new AtomicBoolean(false);

    public Connection(InetAddress address, AsynchronousSocketChannel channel, long lastSeen) {
        this.address = address;
        this.channel = channel;
        this.lastSeen = lastSeen;
    }
}
