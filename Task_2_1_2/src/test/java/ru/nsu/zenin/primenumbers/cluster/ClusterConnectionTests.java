package ru.nsu.zenin.primenumbers.cluster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.primenumbers.cluster.protocol.Codec;
import ru.nsu.zenin.primenumbers.cluster.protocol.Message;
import ru.nsu.zenin.primenumbers.cluster.protocol.ProtocolVersion;

class ClusterConnectionTests {

    static class TestClusterConnection extends ClusterConnection {
        final List<int[]> incomingTasks = Collections.synchronizedList(new ArrayList<>());
        final List<CompletableFuture<Boolean>> incomingFutures =
                Collections.synchronizedList(new ArrayList<>());
        volatile boolean isClosed = false;

        public TestClusterConnection(InetSocketAddress groupAddress, InetSocketAddress nodeAddress)
                throws Exception {
            super(groupAddress, nodeAddress);
        }

        @Override
        public void onIncomingTask(int[] nums, CompletableFuture<Boolean> future) {
            incomingTasks.add(nums);
            incomingFutures.add(future);
        }

        @Override
        public void onClose() {
            isClosed = true;
        }
    }

    static class FakeNode implements AutoCloseable {
        final Socket socket;
        final DataInputStream in;
        final DataOutputStream out;
        final UUID nodeId;

        FakeNode(InetSocketAddress clusterAddr) throws Exception {
            nodeId = UUID.randomUUID();
            socket = new Socket(clusterAddr.getAddress(), clusterAddr.getPort());
            socket.setSoTimeout(3000);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            Codec.deserialize(in);
            sendMessage(new Message.Handshake(nodeId));
        }

        Message readMessage() throws Exception {
            while (true) {
                Message msg = Codec.deserialize(in);
                if (msg instanceof Message.Ping) {
                    sendMessage(new Message.Pong());
                    continue;
                }
                if (msg instanceof Message.Pong) continue;
                return msg;
            }
        }

        void sendMessage(Message msg) throws Exception {
            out.write(Codec.serialize(ProtocolVersion.LEET_VER, msg));
            out.flush();
        }

        @Override
        public void close() throws Exception {
            socket.close();
        }
    }

    @Test
    void testMulticastAnnouncementOnStartup() throws Exception {
        int port = 15000 + (int) (Math.random() * 10000);
        InetSocketAddress groupAddr =
                new InetSocketAddress(InetAddress.getByName("224.0.0.1"), port);
        InetSocketAddress localAddr =
                new InetSocketAddress(InetAddress.getByName("127.0.0.1"), port + 1);

        try (MulticastSocket listener = new MulticastSocket(port)) {
            listener.joinGroup(
                    groupAddr,
                    NetworkInterface.getByInetAddress(InetAddress.getByName("127.0.0.1")));

            TestClusterConnection cluster = new TestClusterConnection(groupAddr, localAddr);

            byte[] buf = new byte[512];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            listener.setSoTimeout(2000);

            listener.receive(packet);
            Message msg =
                    Codec.deserialize(packet.getData(), packet.getOffset(), packet.getLength());

            Assertions.assertTrue(msg instanceof Message.Presence);
            Assertions.assertEquals(localAddr.getPort(), ((Message.Presence) msg).port());

            cluster.close();
        }
    }

    @Test
    void testIncomingPresenceTriggersOutgoingConnection() throws Exception {
        int port = 25000 + (int) (Math.random() * 10000);
        InetSocketAddress groupAddr =
                new InetSocketAddress(InetAddress.getByName("224.0.0.1"), port);
        InetSocketAddress localAddr =
                new InetSocketAddress(InetAddress.getByName("127.0.0.1"), port + 1);

        TestClusterConnection cluster = new TestClusterConnection(groupAddr, localAddr);

        try (ServerSocket fakeNodeServer = new ServerSocket(0)) {
            UUID fakeNodeId = UUID.randomUUID();

            try (MulticastSocket udpSock = new MulticastSocket()) {
                NetworkInterface nf =
                        NetworkInterface.getByInetAddress(InetAddress.getByName("127.0.0.1"));
                udpSock.setNetworkInterface(nf);

                byte[] data =
                        Codec.serialize(
                                ProtocolVersion.LEET_VER,
                                new Message.Presence(
                                        fakeNodeId,
                                        InetAddress.getByName("127.0.0.1"),
                                        fakeNodeServer.getLocalPort()));
                DatagramPacket packet = new DatagramPacket(data, data.length, groupAddr);
                udpSock.send(packet);
            }

            fakeNodeServer.setSoTimeout(3000);
            try (Socket incomingSock = fakeNodeServer.accept()) {
                DataInputStream in = new DataInputStream(incomingSock.getInputStream());
                Message handshake = Codec.deserialize(in);

                Assertions.assertTrue(handshake instanceof Message.Handshake);
            }
        } finally {
            cluster.close();
        }
    }

    @Test
    void testEmptySubmitReturnsFalse() throws Exception {
        InetSocketAddress group = new InetSocketAddress(InetAddress.getByName("224.0.0.1"), 30000);
        InetSocketAddress local = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 30001);
        TestClusterConnection cluster = new TestClusterConnection(group, local);

        CompletableFuture<Boolean> fut = cluster.submit(new int[0]);

        Assertions.assertTrue(fut.isDone());
        Assertions.assertFalse(fut.get());

        cluster.close();
    }

    @Test
    void testSubmitFailsIfNoNodesConnected() throws Exception {
        InetSocketAddress group = new InetSocketAddress(InetAddress.getByName("224.0.0.1"), 35000);
        InetSocketAddress local = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 35001);
        TestClusterConnection cluster = new TestClusterConnection(group, local);

        CompletableFuture<Boolean> fut = cluster.submit(new int[] {1, 2, 3});

        Assertions.assertTrue(fut.isCompletedExceptionally());
        ExecutionException ex = Assertions.assertThrows(ExecutionException.class, fut::get);
        Assertions.assertTrue(ex.getCause() instanceof IllegalStateException);
        Assertions.assertEquals("No nodes in network", ex.getCause().getMessage());

        cluster.close();
    }

    @Test
    void testTaskDistributionAndTrueShortCircuit() throws Exception {
        InetSocketAddress group = new InetSocketAddress(InetAddress.getByName("224.0.0.1"), 40000);
        InetSocketAddress local = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 40001);
        TestClusterConnection cluster = new TestClusterConnection(group, local);

        try (FakeNode node1 = new FakeNode(local);
                FakeNode node2 = new FakeNode(local)) {

            Thread.sleep(150);

            CompletableFuture<Boolean> fut = cluster.submit(new int[64]);

            Message.TaskSubmit sub1 = (Message.TaskSubmit) node1.readMessage();
            Message.TaskSubmit sub2 = (Message.TaskSubmit) node2.readMessage();

            node1.sendMessage(new Message.TaskResult(sub1.taskId(), true));

            Assertions.assertTrue(fut.get(2, TimeUnit.SECONDS));

            Message stopMsg = node2.readMessage();
            Assertions.assertTrue(stopMsg instanceof Message.TaskStop);
            Assertions.assertEquals(sub2.taskId(), ((Message.TaskStop) stopMsg).taskId());
        }
        cluster.close();
    }

    @Test
    void testTaskRetryOnNodeFault() throws Exception {
        InetSocketAddress group = new InetSocketAddress(InetAddress.getByName("224.0.0.1"), 45000);
        InetSocketAddress local = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 45001);
        TestClusterConnection cluster = new TestClusterConnection(group, local);

        try (FakeNode node1 = new FakeNode(local);
                FakeNode node2 = new FakeNode(local)) {

            Thread.sleep(150);

            CompletableFuture<Boolean> fut = cluster.submit(new int[64]);

            Message.TaskSubmit sub1 = (Message.TaskSubmit) node1.readMessage();
            Message.TaskSubmit sub2 = (Message.TaskSubmit) node2.readMessage();

            node1.sendMessage(new Message.TaskFailed(sub1.taskId()));

            Message.TaskSubmit sub1_retry = (Message.TaskSubmit) node2.readMessage();

            Assertions.assertNotEquals(sub1.taskId(), sub1_retry.taskId());
            Assertions.assertEquals(32, sub1_retry.numbers().length);

            node2.sendMessage(new Message.TaskResult(sub2.taskId(), false));
            node2.sendMessage(new Message.TaskResult(sub1_retry.taskId(), false));

            Assertions.assertFalse(fut.get(2, TimeUnit.SECONDS));
        }
        cluster.close();
    }

    @Test
    void testIncomingTaskRouting() throws Exception {
        InetSocketAddress group = new InetSocketAddress(InetAddress.getByName("224.0.0.1"), 50000);
        InetSocketAddress local = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 50001);
        TestClusterConnection cluster = new TestClusterConnection(group, local);

        try (FakeNode node = new FakeNode(local)) {
            Thread.sleep(150);

            UUID taskId = UUID.randomUUID();
            int[] nums = {99, 100, 101};
            node.sendMessage(new Message.TaskSubmit(taskId, nums));

            long start = System.currentTimeMillis();
            while (cluster.incomingTasks.isEmpty() && System.currentTimeMillis() - start < 2000) {
                Thread.sleep(10);
            }

            Assertions.assertFalse(cluster.incomingTasks.isEmpty());
            Assertions.assertArrayEquals(nums, cluster.incomingTasks.get(0));

            cluster.incomingFutures.get(0).complete(true);

            Message response = node.readMessage();
            Assertions.assertTrue(response instanceof Message.TaskResult);
            Assertions.assertTrue(((Message.TaskResult) response).hasComposite());
            Assertions.assertEquals(taskId, ((Message.TaskResult) response).taskId());
        }
        cluster.close();
    }

    @Test
    void testSubmitFailsExceptionallyWhenSubfutureFailsAndNoNodesAvailable() throws Exception {
        int port = 55000 + (int) (Math.random() * 10000);
        InetSocketAddress groupAddr =
                new InetSocketAddress(InetAddress.getByName("224.0.0.1"), port);
        InetSocketAddress localAddr =
                new InetSocketAddress(InetAddress.getByName("127.0.0.1"), port + 1);

        TestClusterConnection cluster = new TestClusterConnection(groupAddr, localAddr);

        try (FakeNode node = new FakeNode(localAddr)) {
            Thread.sleep(150);

            CompletableFuture<Boolean> mainFut = cluster.submit(new int[] {2, 3, 5, 7});

            Message.TaskSubmit submittedTask = (Message.TaskSubmit) node.readMessage();

            node.sendMessage(new Message.TaskFailed(submittedTask.taskId()));

            ExecutionException ex =
                    Assertions.assertThrows(
                            ExecutionException.class, () -> mainFut.get(2, TimeUnit.SECONDS));

            Assertions.assertTrue(ex.getCause() instanceof IllegalStateException);
            Assertions.assertEquals("No nodes in network", ex.getCause().getMessage());
        } finally {
            cluster.close();
        }
    }
}
