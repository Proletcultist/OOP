package ru.nsu.zenin.primenumbers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import ru.nsu.zenin.primenumbers.cluster.ClusterConnection;
import ru.nsu.zenin.primenumbers.logging.Logger;

public class App {
    public static void main(String[] args) throws Exception {
        InetSocketAddress group = new InetSocketAddress("224.0.0.70", 9090);

        Logger.init(new BufferedWriter(new OutputStreamWriter(System.err)));

        ClusterConnection con1 =
                new ClusterConnection(group, new InetSocketAddress("127.0.0.1", 12345)) {
                    @Override
                    public void onIncomingTask(int[] nums, CompletableFuture<Boolean> future) {
                        System.out.println("Received task: " + Arrays.toString(nums));
                    }

                    @Override
                    public void onClose() {}
                };

        ClusterConnection con2 =
                new ClusterConnection(group, new InetSocketAddress("127.0.0.1", 54321)) {
                    @Override
                    public void onIncomingTask(int[] nums, CompletableFuture<Boolean> future) {
                        System.out.println("Received task: " + Arrays.toString(nums));
                        future.complete(false);
                    }

                    @Override
                    public void onClose() {}
                };

        Node node =
                new Node(group, new InetSocketAddress("127.0.0.1", 53321)) {
                    @Override
                    protected void onShutdown() {
                        System.exit(0);
                    }
                };

        try (Reader reader = new BufferedReader(new InputStreamReader(System.in));
                Writer writer = new OutputStreamWriter(System.out)) {
            node.serviceRepl(reader, writer);
        }

        Logger.close();
    }
}
