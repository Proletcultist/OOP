package ru.nsu.zenin.primenumbers;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import ru.nsu.zenin.primenumbers.cluster.ClusterConnection;

public class App {
    public static void main(String[] args) throws Exception {
        InetSocketAddress group = new InetSocketAddress("224.0.0.70", 9090);

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
                        future.complete(true);
                    }

                    @Override
                    public void onClose() {}
                };

        ClusterConnection con3 =
                new ClusterConnection(group, new InetSocketAddress("127.0.0.1", 53321)) {
                    @Override
                    public void onIncomingTask(int[] nums, CompletableFuture<Boolean> future) {
                        System.out.println("Received task: " + Arrays.toString(nums));
                        future.complete(false);
                    }

                    @Override
                    public void onClose() {}
                };

        Thread.sleep(1000);

        int[] nums = {3, 2, 1, 3, 5};
        System.out.println(con1.submit(nums).get());
    }
}
