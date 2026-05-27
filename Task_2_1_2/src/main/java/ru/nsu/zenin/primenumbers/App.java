package ru.nsu.zenin.primenumbers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import ru.nsu.zenin.primenumbers.logging.Logger;

public class App {
    public static void main(String[] args) throws Exception {
        InetSocketAddress group = new InetSocketAddress("224.0.0.70", 9090);

        Logger.init(new BufferedWriter(new OutputStreamWriter(System.err)));

        Node b_node1 =
                new Node(group, new InetSocketAddress("127.0.0.1", 12345)) {
                    @Override
                    protected void onShutdown() {}
                };

        Node b_node2 =
                new Node(group, new InetSocketAddress("127.0.0.1", 54321)) {
                    @Override
                    protected void onShutdown() {}
                };

        Node node =
                new Node(group, new InetSocketAddress("127.0.0.1", 53321)) {
                    @Override
                    protected void onShutdown() {
                        Logger.close();
                        System.exit(0);
                    }
                };

        try (Reader reader = new BufferedReader(new InputStreamReader(System.in));
                Writer writer = new OutputStreamWriter(System.out)) {
            node.serviceRepl(reader, writer);
        }

        // If repl ended - shutdown node
        node.shutdown();
    }
}
