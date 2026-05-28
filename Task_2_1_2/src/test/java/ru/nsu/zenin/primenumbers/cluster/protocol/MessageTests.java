package ru.nsu.zenin.primenumbers.cluster.protocol;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MessageTests {
    @Test
    void testPrescence() throws Exception {
        UUID nodeId = UUID.randomUUID();
        InetAddress addr = InetAddress.getByName("127.0.0.1");

        Message.Presence msg = new Message.Presence(nodeId, addr, 1080);

        Assertions.assertEquals(msg.nodeId(), nodeId);
        Assertions.assertEquals(msg.address(), addr);
        Assertions.assertEquals(msg.port(), 1080);
        Assertions.assertEquals(msg.getType(), MessageType.PRESENCE);
    }

    @Test
    void testSubmit() throws Exception {
        UUID taskId = UUID.randomUUID();
        int[] nums = {1, 2, 3};

        Message.TaskSubmit msg = new Message.TaskSubmit(taskId, nums);

        Assertions.assertEquals(msg.taskId(), taskId);
        Assertions.assertTrue(Arrays.equals(msg.numbers(), nums));
        Assertions.assertEquals(msg.getType(), MessageType.TASK_SUBMIT);
    }

    @Test
    void testResult() throws Exception {
        UUID taskId = UUID.randomUUID();

        Message.TaskResult msg = new Message.TaskResult(taskId, true);

        Assertions.assertEquals(msg.taskId(), taskId);
        Assertions.assertTrue(msg.hasComposite());
        Assertions.assertEquals(msg.getType(), MessageType.TASK_RESULT);
    }

    @Test
    void testStop() throws Exception {
        UUID taskId = UUID.randomUUID();

        Message.TaskStop msg = new Message.TaskStop(taskId);

        Assertions.assertEquals(msg.taskId(), taskId);
        Assertions.assertEquals(msg.getType(), MessageType.TASK_STOP);
    }

    @Test
    void testFailed() throws Exception {
        UUID taskId = UUID.randomUUID();

        Message.TaskFailed msg = new Message.TaskFailed(taskId);

        Assertions.assertEquals(msg.taskId(), taskId);
        Assertions.assertEquals(msg.getType(), MessageType.TASK_FAILED);
    }

    @Test
    void testPing() throws Exception {
        Message.Ping msg = new Message.Ping();

        Assertions.assertEquals(msg.getType(), MessageType.PING);
    }

    @Test
    void testPong() throws Exception {
        Message.Pong msg = new Message.Pong();

        Assertions.assertEquals(msg.getType(), MessageType.PONG);
    }

    @Test
    void testHandshake() throws Exception {
        UUID nodeId = UUID.randomUUID();

        Message.Handshake msg = new Message.Handshake(nodeId);

        Assertions.assertEquals(msg.nodeId(), nodeId);
        Assertions.assertEquals(msg.getType(), MessageType.HANDSHAKE);
    }
}
