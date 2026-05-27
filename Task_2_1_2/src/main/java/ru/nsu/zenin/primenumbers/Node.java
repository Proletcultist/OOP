package ru.nsu.zenin.primenumbers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import ru.nsu.zenin.primenumbers.cluster.ClusterConnection;

public abstract class Node {
    private final ClusterConnection connection;
    private final ExecutorService computationsExecutor;
    private final Map<Integer, Boolean> cache;

    private final AtomicBoolean working;

    protected abstract void onShutdown();

    public Node(InetSocketAddress groupAddr, InetSocketAddress nodeAddr) throws IOException {
        cache = new ConcurrentHashMap<Integer, Boolean>();
        computationsExecutor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        working = new AtomicBoolean(true);

        try {
            this.connection =
                    new ClusterConnection(groupAddr, nodeAddr) {
                        @Override
                        public void onIncomingTask(int[] nums, CompletableFuture<Boolean> future) {
                            future.completeAsync(() -> compute(nums, future), computationsExecutor);
                        }

                        @Override
                        public void onClose() {
                            shutdown();
                        }
                    };
        } catch (IOException e) {
            shutdown();
            throw e;
        }
    }

    public void serviceRepl(Reader is, Writer os) {
        try (PrintWriter out = new PrintWriter(os, true);
                Scanner in = new Scanner(is)) {
            try {
                while (true) {
                    out.print("> ");
                    out.flush();

                    String command = in.next();

                    switch (command) {
                        case "check" -> {
                            try {
                                out.println(connection.submit(readIntsArray(in)).get());
                            } catch (Exception e) {
                                out.println("Failed to check: " + e.getMessage());
                            }
                        }
                        case "shutdown" -> {
                            shutdown();
                            break;
                        }
                        default -> {
                            out.println("Unknown command");
                        }
                    }
                }
            }
            // If underlying stream was closed - just stop repl
            catch (Exception ignore) {
            }
        }
    }

    public boolean shutdown() {
        if (working.compareAndSet(true, false)) {
            if (connection != null) {
                connection.close();
            }
            computationsExecutor.shutdown();

            onShutdown();

            return true;
        } else {
            return false;
        }
    }

    private int[] readIntsArray(Scanner sc) {
        String line = sc.nextLine().trim();
        return Stream.of(line.split("\\s+")).mapToInt(s -> Integer.parseInt(s)).toArray();
    }

    private boolean compute(int[] nums, CompletableFuture<Boolean> fut) {
        for (int num : nums) {
            // If fut is already cancelled we can safely return anything
            if (fut.isCancelled()) {
                return false;
            }

            if (isNumCompound(num)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNumCompound(int n) {
        Boolean lookup = cache.get(n);
        if (lookup != null) {
            return lookup;
        }

        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                cache.put(n, true);
                return true;
            }
        }

        cache.put(n, false);
        return false;
    }
}
