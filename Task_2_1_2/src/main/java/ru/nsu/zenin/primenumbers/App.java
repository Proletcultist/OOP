package ru.nsu.zenin.primenumbers;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import ru.nsu.zenin.primenumbers.cluster.ClusterConnection;

public class App {
    public static void main(String[] args) throws Exception {
        InetSocketAddress group = new InetSocketAddress("224.0.0.70", 9090);

        ClusterConnection con1 =
                new ClusterConnection(group, new InetSocketAddress("127.0.0.1", 12345)) {
                    @Override
                    public void onIncomingTask(int[] nums, CompletableFuture<Boolean> future) {}

                    @Override
                    public void onClose() {}
                };

        ClusterConnection con2 =
                new ClusterConnection(group, new InetSocketAddress("127.0.0.1", 54321)) {
                    @Override
                    public void onIncomingTask(int[] nums, CompletableFuture<Boolean> future) {}

                    @Override
                    public void onClose() {}
                };

        while (true) {}
    }
}
