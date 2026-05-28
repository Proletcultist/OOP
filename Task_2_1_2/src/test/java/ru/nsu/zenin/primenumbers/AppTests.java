package ru.nsu.zenin.primenumbers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.primenumbers.cluster.ClusterConnection;

class AppTests {

    private static class SyncedOutputStream extends OutputStream {
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        @Override
        public synchronized void write(int b) {
            buffer.write(b);
            notifyAll();
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) {
            buffer.write(b, off, len);
            notifyAll();
        }

        public synchronized void waitForSubstring(String expected, long timeoutMs)
                throws InterruptedException, TimeoutException {
            long deadline = System.currentTimeMillis() + timeoutMs;

            while (!buffer.toString(StandardCharsets.UTF_8).contains(expected)) {
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) {
                    throw new TimeoutException(
                            "Timed out waiting for '"
                                    + expected
                                    + "'. Uutput:\n"
                                    + buffer.toString(StandardCharsets.UTF_8));
                }
                wait(remaining);
            }
        }

        @Override
        public synchronized String toString() {
            return buffer.toString(StandardCharsets.UTF_8);
        }
    }

    private static class WorkerConnection extends ClusterConnection {
        public WorkerConnection(InetSocketAddress groupAddr, InetSocketAddress nodeAddr)
                throws IOException {
            super(groupAddr, nodeAddr);
        }

        @Override
        public void onIncomingTask(int[] nums, CompletableFuture<Boolean> future) {
            boolean hasComposite = false;
            for (int n : nums) {
                if (n > 1) {
                    for (int i = 2; i * i <= n; i++) {
                        if (n % i == 0) {
                            hasComposite = true;
                            break;
                        }
                    }
                }
                if (hasComposite) break;
            }
            future.complete(hasComposite);
        }

        @Override
        public void onClose() {}
    }

    @Test
    void testReplAppInteractsWithDirectClusterConnections() throws Exception {
        int mcastPort = 40000 + (int) (Math.random() * 10000);
        String groupAddrStr = "224.0.0.1:" + mcastPort;
        InetSocketAddress groupSocketAddr = new InetSocketAddress("224.0.0.1", mcastPort);

        WorkerConnection worker1 = null;
        WorkerConnection worker2 = null;

        InputStream oldIn = System.in;
        PrintStream oldOut = System.out;

        try {
            worker1 =
                    new WorkerConnection(
                            groupSocketAddr, new InetSocketAddress("127.0.0.1", mcastPort + 1));
            worker2 =
                    new WorkerConnection(
                            groupSocketAddr, new InetSocketAddress("127.0.0.1", mcastPort + 2));

            PipedOutputStream inputWriter = new PipedOutputStream();
            PipedInputStream pipedIn = new PipedInputStream(inputWriter);
            System.setIn(pipedIn);

            SyncedOutputStream syncedOut = new SyncedOutputStream();
            System.setOut(new PrintStream(syncedOut, true));

            String replNodeAddr = "127.0.0.1:" + (mcastPort + 3);
            Thread appThread =
                    new Thread(
                            () -> {
                                try {
                                    App.main(new String[] {"repl", groupAddrStr, replNodeAddr});
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
            appThread.start();

            Thread.sleep(1500);

            int[] largeTask = new int[100];
            Arrays.fill(largeTask, 4);

            CompletableFuture<Boolean> remoteSubmittedTask = worker1.submit(largeTask);
            Boolean clusterResult = remoteSubmittedTask.get(5, TimeUnit.SECONDS);

            Assertions.assertTrue(clusterResult);

            inputWriter.write("check 3 7 15\n".getBytes(StandardCharsets.UTF_8));
            inputWriter.flush();
            syncedOut.waitForSubstring("true", 5000);

            String absoluteResourcePath =
                    Path.of(getClass().getClassLoader().getResource("test_nums.txt").toURI())
                            .toAbsolutePath()
                            .toString();
            String fileCommand = "checkf " + absoluteResourcePath + "\n";
            inputWriter.write(fileCommand.getBytes(StandardCharsets.UTF_8));
            inputWriter.flush();
            syncedOut.waitForSubstring("false", 5000);

            inputWriter.write("shutdown\n".getBytes(StandardCharsets.UTF_8));
            inputWriter.flush();
            inputWriter.close();

            appThread.join(5000);
            Assertions.assertFalse(appThread.isAlive());

        } finally {
            System.setIn(oldIn);
            System.setOut(oldOut);

            if (worker1 != null) worker1.close();
            if (worker2 != null) worker2.close();
        }
    }

    @Test
    void testHeadlessNodeProcessesIncomingClusterTasks() throws Exception {
        int mcastPort = 50000 + (int) (Math.random() * 10000);
        String groupAddrStr = "224.0.0.1:" + mcastPort;
        InetSocketAddress groupSocketAddr = new InetSocketAddress("224.0.0.1", mcastPort);

        WorkerConnection submitter = null;
        Thread headlessThread = null;

        try {
            submitter =
                    new WorkerConnection(
                            groupSocketAddr, new InetSocketAddress("127.0.0.1", mcastPort + 1));

            String headlessNodeAddr = "127.0.0.1:" + (mcastPort + 2);
            headlessThread =
                    new Thread(
                            () -> {
                                try {
                                    App.main(
                                            new String[] {
                                                "headless", groupAddrStr, headlessNodeAddr
                                            });
                                } catch (InterruptedException e) {
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
            headlessThread.start();

            Thread.sleep(1500);

            int[] largeTask = new int[100];
            java.util.Arrays.fill(largeTask, 4);

            CompletableFuture<Boolean> clusterTaskResult = submitter.submit(largeTask);
            Boolean evaluation = clusterTaskResult.get(6, TimeUnit.SECONDS);

            Assertions.assertTrue(evaluation);

        } finally {
            if (submitter != null) {
                submitter.close();
            }
            if (headlessThread != null) {
                headlessThread.interrupt();
                headlessThread.join(5000);
            }
        }
    }
}
