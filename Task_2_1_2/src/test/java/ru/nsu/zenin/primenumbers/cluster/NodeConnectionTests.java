package ru.nsu.zenin.primenumbers.cluster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.primenumbers.cluster.protocol.Codec;
import ru.nsu.zenin.primenumbers.cluster.protocol.Message;
import ru.nsu.zenin.primenumbers.cluster.protocol.ProtocolVersion;

class NodeConnectionTests {

    private ServerSocket serverSocket;
    private Socket peerSocket;
    private Socket nodeSocket;
    private DataInputStream peerIn;
    private DataOutputStream peerOut;

    @BeforeEach
    void setUp() throws Exception {
        serverSocket = new ServerSocket(0);
        peerSocket = new Socket("127.0.0.1", serverSocket.getLocalPort());
        nodeSocket = serverSocket.accept();
        peerIn = new DataInputStream(peerSocket.getInputStream());
        peerOut = new DataOutputStream(peerSocket.getOutputStream());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (peerSocket != null) peerSocket.close();
        if (nodeSocket != null) nodeSocket.close();
        if (serverSocket != null) serverSocket.close();
    }

    static class TestNodeConnection extends NodeConnection {
        final List<State> stateChanges = Collections.synchronizedList(new ArrayList<>());
        final List<int[]> incomingTasks = Collections.synchronizedList(new ArrayList<>());
        final List<CompletableFuture<Boolean>> incomingFutures =
                Collections.synchronizedList(new ArrayList<>());

        public TestNodeConnection(
                ProtocolVersion ver, Socket socket, UUID localNodeId, boolean outgoing)
                throws IOException, InterruptedException {
            super(ver, socket, localNodeId, outgoing);
        }

        @Override
        protected void onStateChange(State state) {
            stateChanges.add(state);
        }

        @Override
        protected void onIncomingTask(int[] nums, CompletableFuture<Boolean> future) {
            incomingTasks.add(nums);
            incomingFutures.add(future);
        }
    }

    @Test
    void testInitializationSendsHandshake() throws Exception {
        UUID localId = UUID.randomUUID();
        TestNodeConnection conn =
                new TestNodeConnection(ProtocolVersion.LEET_VER, nodeSocket, localId, true);

        Message msg = Codec.deserialize(peerIn);
        Assertions.assertTrue(msg instanceof Message.Handshake);
        Assertions.assertEquals(localId, ((Message.Handshake) msg).nodeId());

        conn.close();
    }

    @Test
    void testIncomingHandshakeIdentifiesConnection() throws Exception {
        UUID localId = UUID.randomUUID();
        UUID remoteId = UUID.randomUUID();
        TestNodeConnection conn =
                new TestNodeConnection(ProtocolVersion.LEET_VER, nodeSocket, localId, true);

        Codec.deserialize(peerIn);

        Codec.serialize(peerOut, ProtocolVersion.LEET_VER, new Message.Handshake(remoteId));

        Thread.sleep(50);

        Assertions.assertEquals(remoteId, conn.getRemoteNodeId());
        Assertions.assertTrue(conn.stateChanges.contains(NodeConnection.State.IDENTIFIED));

        conn.close();
    }

    @Test
    void testInvalidFirstMessageClosesConnection() throws Exception {
        UUID localId = UUID.randomUUID();
        TestNodeConnection conn =
                new TestNodeConnection(ProtocolVersion.LEET_VER, nodeSocket, localId, true);
        Codec.deserialize(peerIn);

        Codec.serialize(peerOut, ProtocolVersion.LEET_VER, new Message.Ping());
        Thread.sleep(50);

        Assertions.assertTrue(conn.stateChanges.contains(NodeConnection.State.DISCONNECTED));
        conn.close();
    }

    @Test
    void testSubmitTaskSendsMessage() throws Exception {
        UUID localId = UUID.randomUUID();
        TestNodeConnection conn =
                new TestNodeConnection(ProtocolVersion.LEET_VER, nodeSocket, localId, true);
        Codec.deserialize(peerIn);

        int[] nums = {11, 13, 17};

        CompletableFuture<Boolean> future = conn.submit(nums);

        Message msg = Codec.deserialize(peerIn);
        Assertions.assertTrue(msg instanceof Message.TaskSubmit);
        Message.TaskSubmit submitMsg = (Message.TaskSubmit) msg;
        Assertions.assertArrayEquals(nums, submitMsg.numbers());
        Assertions.assertFalse(future.isDone());

        conn.close();
    }

    @Test
    void testReceiveTaskSubmitAndCompleteResult() throws Exception {
        UUID localId = UUID.randomUUID();
        TestNodeConnection conn =
                new TestNodeConnection(ProtocolVersion.LEET_VER, nodeSocket, localId, true);
        Codec.deserialize(peerIn);

        Codec.serialize(
                peerOut, ProtocolVersion.LEET_VER, new Message.Handshake(UUID.randomUUID()));
        Thread.sleep(50);

        UUID taskId = UUID.randomUUID();
        int[] nums = {23, 29};
        Codec.serialize(peerOut, ProtocolVersion.LEET_VER, new Message.TaskSubmit(taskId, nums));
        Thread.sleep(50);

        Assertions.assertEquals(1, conn.incomingTasks.size());
        Assertions.assertArrayEquals(nums, conn.incomingTasks.get(0));

        conn.incomingFutures.get(0).complete(true);
        Message response = Codec.deserialize(peerIn);
        Assertions.assertTrue(response instanceof Message.TaskResult);
        Assertions.assertEquals(taskId, ((Message.TaskResult) response).taskId());
        Assertions.assertTrue(((Message.TaskResult) response).hasComposite());

        conn.close();
    }

    @Test
    void testReceiveTaskResultCompletesLocalFuture() throws Exception {
        UUID localId = UUID.randomUUID();
        TestNodeConnection conn =
                new TestNodeConnection(ProtocolVersion.LEET_VER, nodeSocket, localId, true);
        Codec.deserialize(peerIn);
        Codec.serialize(
                peerOut, ProtocolVersion.LEET_VER, new Message.Handshake(UUID.randomUUID()));
        Thread.sleep(50);

        CompletableFuture<Boolean> future = conn.submit(new int[] {31});
        Message.TaskSubmit submitMsg = (Message.TaskSubmit) Codec.deserialize(peerIn);

        Codec.serialize(
                peerOut,
                ProtocolVersion.LEET_VER,
                new Message.TaskResult(submitMsg.taskId(), false));

        Boolean result = future.get(1, TimeUnit.SECONDS);
        Assertions.assertFalse(result);

        conn.close();
    }

    @Test
    void testReceiveTaskFailedCompletesExceptionally() throws Exception {
        UUID localId = UUID.randomUUID();
        TestNodeConnection conn =
                new TestNodeConnection(ProtocolVersion.LEET_VER, nodeSocket, localId, true);
        Codec.deserialize(peerIn);
        Codec.serialize(
                peerOut, ProtocolVersion.LEET_VER, new Message.Handshake(UUID.randomUUID()));
        Thread.sleep(50);

        CompletableFuture<Boolean> future = conn.submit(new int[] {37});
        Message.TaskSubmit submitMsg = (Message.TaskSubmit) Codec.deserialize(peerIn);

        Codec.serialize(
                peerOut, ProtocolVersion.LEET_VER, new Message.TaskFailed(submitMsg.taskId()));

        Assertions.assertThrows(ExecutionException.class, () -> future.get(1, TimeUnit.SECONDS));

        conn.close();
    }

    @Test
    void testPingRespondsWithPong() throws Exception {
        UUID localId = UUID.randomUUID();
        TestNodeConnection conn =
                new TestNodeConnection(ProtocolVersion.LEET_VER, nodeSocket, localId, true);
        Codec.deserialize(peerIn);
        Codec.serialize(
                peerOut, ProtocolVersion.LEET_VER, new Message.Handshake(UUID.randomUUID()));
        Thread.sleep(50);

        Codec.serialize(peerOut, ProtocolVersion.LEET_VER, new Message.Ping());

        Message response = Codec.deserialize(peerIn);
        Assertions.assertTrue(response instanceof Message.Pong);

        conn.close();
    }

    @Test
    void testCloseCancelsPendingTasksAndUpdatesState() throws Exception {
        UUID localId = UUID.randomUUID();
        TestNodeConnection conn =
                new TestNodeConnection(ProtocolVersion.LEET_VER, nodeSocket, localId, true);
        Codec.deserialize(peerIn);

        CompletableFuture<Boolean> future = conn.submit(new int[] {41, 43});

        conn.close();

        Assertions.assertTrue(conn.stateChanges.contains(NodeConnection.State.DISCONNECTED));
        Assertions.assertThrows(ExecutionException.class, () -> future.get(1, TimeUnit.SECONDS));
    }

    @Test
    void testCancellationOfSubmittedTaskCancelsRemoteFuture() throws Exception {
        try (ServerSocket ss = new ServerSocket(0);
                Socket s1 = new Socket("127.0.0.1", ss.getLocalPort());
                Socket s2 = ss.accept()) {

            TestNodeConnection conn1 =
                    new TestNodeConnection(ProtocolVersion.LEET_VER, s1, UUID.randomUUID(), true);
            TestNodeConnection conn2 =
                    new TestNodeConnection(ProtocolVersion.LEET_VER, s2, UUID.randomUUID(), false);

            CompletableFuture<Boolean> submitFut = conn1.submit(new int[] {1, 2, 3});

            long start = System.currentTimeMillis();
            while (conn2.incomingFutures.isEmpty() && System.currentTimeMillis() - start < 5000) {
                Thread.sleep(50);
            }

            Assertions.assertFalse(conn2.incomingFutures.isEmpty());
            CompletableFuture<Boolean> receivedFut = conn2.incomingFutures.get(0);

            submitFut.cancel(true);

            start = System.currentTimeMillis();
            while (!receivedFut.isCancelled() && System.currentTimeMillis() - start < 5000) {
                Thread.sleep(50);
            }
            Assertions.assertTrue(receivedFut.isCancelled());

            conn1.close();
            conn2.close();
        }
    }

    @Test
    void testSubmitReturnsExceptionallyIfConnectionIsClosed() throws Exception {
        try (ServerSocket ss = new ServerSocket(0);
                Socket s1 = new Socket("127.0.0.1", ss.getLocalPort());
                Socket s2 = ss.accept()) {

            TestNodeConnection conn =
                    new TestNodeConnection(ProtocolVersion.LEET_VER, s1, UUID.randomUUID(), true);

            conn.close();
            CompletableFuture<Boolean> submitFut = conn.submit(new int[] {10, 20});

            Assertions.assertTrue(submitFut.isCompletedExceptionally());
            ExecutionException ex =
                    Assertions.assertThrows(
                            ExecutionException.class, () -> submitFut.get(1, TimeUnit.SECONDS));
            Assertions.assertTrue(ex.getCause() instanceof IOException);
            Assertions.assertEquals("Connection is down", ex.getCause().getMessage());

            s2.close();
        }
    }

    @Test
    void testConnectionClosesIfFirstMessageIsNotHandshake() throws Exception {
        try (ServerSocket ss = new ServerSocket(0);
                Socket rawSocket = new Socket("127.0.0.1", ss.getLocalPort());
                Socket connSocket = ss.accept()) {

            TestNodeConnection conn =
                    new TestNodeConnection(
                            ProtocolVersion.LEET_VER, connSocket, UUID.randomUUID(), false);

            DataInputStream rawIn = new DataInputStream(rawSocket.getInputStream());
            Codec.deserialize(rawIn);

            DataOutputStream rawOut = new DataOutputStream(rawSocket.getOutputStream());
            byte[] badFirstMessageBytes =
                    Codec.serialize(ProtocolVersion.LEET_VER, new Message.Ping());
            rawOut.write(badFirstMessageBytes);
            rawOut.flush();

            long start = System.currentTimeMillis();
            while (!conn.stateChanges.contains(NodeConnection.State.DISCONNECTED)
                    && System.currentTimeMillis() - start < 2000) {
                Thread.sleep(10);
            }
            Assertions.assertTrue(conn.stateChanges.contains(NodeConnection.State.DISCONNECTED));

            conn.close();
        }
    }

    @Test
    void testExceptionalCompletionOfReceivedFutureFailsSubmittedTask() throws Exception {
        try (ServerSocket ss = new ServerSocket(0);
                Socket s1 = new Socket("127.0.0.1", ss.getLocalPort());
                Socket s2 = ss.accept()) {

            TestNodeConnection conn1 =
                    new TestNodeConnection(ProtocolVersion.LEET_VER, s1, UUID.randomUUID(), true);
            TestNodeConnection conn2 =
                    new TestNodeConnection(ProtocolVersion.LEET_VER, s2, UUID.randomUUID(), false);

            CompletableFuture<Boolean> submitFut = conn1.submit(new int[] {100, 200});

            long start = System.currentTimeMillis();
            while (conn2.incomingFutures.isEmpty() && System.currentTimeMillis() - start < 5000) {
                Thread.sleep(50);
            }

            Assertions.assertFalse(conn2.incomingFutures.isEmpty());

            CompletableFuture<Boolean> receivedFut = conn2.incomingFutures.get(0);

            receivedFut.completeExceptionally(new RuntimeException("Computation error"));

            ExecutionException ex =
                    Assertions.assertThrows(
                            ExecutionException.class, () -> submitFut.get(5, TimeUnit.SECONDS));

            Assertions.assertTrue(
                    ex.getCause()
                            instanceof
                            ru.nsu.zenin.primenumbers.cluster.exception.NodeFaultException);
            Assertions.assertEquals("Node fault", ex.getCause().getMessage());

            conn1.close();
            conn2.close();
        }
    }

    @Test
    void testConnectionClosesOnHeartbeatTimeout() throws Exception {
        try (ServerSocket ss = new ServerSocket(0);
                Socket rawSocket = new Socket("127.0.0.1", ss.getLocalPort());
                Socket connSocket = ss.accept()) {

            TestNodeConnection conn =
                    new TestNodeConnection(
                            ProtocolVersion.LEET_VER, connSocket, UUID.randomUUID(), false);

            DataInputStream rawIn = new DataInputStream(rawSocket.getInputStream());
            DataOutputStream rawOut = new DataOutputStream(rawSocket.getOutputStream());

            Codec.deserialize(rawIn);

            byte[] handshakeBytes =
                    Codec.serialize(
                            ProtocolVersion.LEET_VER, new Message.Handshake(UUID.randomUUID()));
            rawOut.write(handshakeBytes);
            rawOut.flush();

            long start = System.currentTimeMillis();
            while (!conn.stateChanges.contains(NodeConnection.State.DISCONNECTED)
                    && System.currentTimeMillis() - start < 6000) {
                Thread.sleep(50);
            }

            Assertions.assertTrue(conn.stateChanges.contains(NodeConnection.State.DISCONNECTED));

            conn.close();
        }
    }
}
