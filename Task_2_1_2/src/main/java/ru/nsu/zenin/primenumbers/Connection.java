package ru.nsu.zenin.primenumbers;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Connection {
    private final AsynchronousSocketChannel channel;
    private long lastSeen;
    private List<Task> submittedTasks = new ArrayList<Task>();

    public Connection(AsynchronousSocketChannel channel, long lastSeen) {
        this.channel = channel;
        this.lastSeen = lastSeen;
    }
}
