package ru.nsu.zenin.primenumbers.cluster.protocol;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.primenumbers.cluster.protocol.exception.UnknownMessageTypeException;
import ru.nsu.zenin.primenumbers.cluster.protocol.exception.UnknownVersionException;
import ru.nsu.zenin.primenumbers.cluster.protocol.exception.WrongMagicNumberException;

class CodecTests {
    private static final byte[] MESSAGE_BEGIN = {
        (byte) 80, (byte) 78, (byte) 80, (byte) 0b00000101, (byte) 0b00111001
    };
    private static final ProtocolVersion VER = ProtocolVersion.LEET_VER;

    @Test
    void testPresence() throws Exception {
        UUID nodeId = new UUID(1L, 2L);
        InetAddress addr = InetAddress.getByName("127.0.0.1");
        int port = 1080;

        Message.Presence msg = new Message.Presence(nodeId, addr, port);

        byte[] expected =
                ByteBuffer.allocate(Codec.getSerializedSize(msg))
                        .put(MESSAGE_BEGIN)
                        .put(msg.getType().getCode())
                        .putLong(nodeId.getMostSignificantBits())
                        .putLong(nodeId.getLeastSignificantBits())
                        .put((byte) addr.getAddress().length)
                        .put(addr.getAddress())
                        .putInt(port)
                        .array();

        byte[] actual = Codec.serialize(VER, msg);
        Assertions.assertArrayEquals(expected, actual);

        Message deserialized = Codec.deserialize(actual, 0, actual.length);
        Assertions.assertEquals(msg, deserialized);
    }

    @Test
    void testSubmit() throws Exception {
        UUID taskId = new UUID(3L, 4L);
        int[] nums = {1, 2, 3};

        Message.TaskSubmit msg = new Message.TaskSubmit(taskId, nums);

        ByteBuffer buffer =
                ByteBuffer.allocate(Codec.getSerializedSize(msg))
                        .put(MESSAGE_BEGIN)
                        .put(msg.getType().getCode())
                        .putLong(taskId.getMostSignificantBits())
                        .putLong(taskId.getLeastSignificantBits())
                        .putInt(nums.length);
        for (int num : nums) {
            buffer.putInt(num);
        }
        byte[] expected = buffer.array();

        byte[] actual = Codec.serialize(VER, msg);
        Assertions.assertArrayEquals(expected, actual);

        Message deserialized = Codec.deserialize(actual, 0, actual.length);
        Assertions.assertTrue(deserialized instanceof Message.TaskSubmit);

        Message.TaskSubmit deserializedSubmit = (Message.TaskSubmit) deserialized;
        Assertions.assertEquals(msg.taskId(), deserializedSubmit.taskId());
        Assertions.assertArrayEquals(msg.numbers(), deserializedSubmit.numbers());
        Assertions.assertEquals(msg.getType(), deserializedSubmit.getType());
    }

    @Test
    void testResult() throws Exception {
        UUID taskId = new UUID(5L, 6L);

        Message.TaskResult msg = new Message.TaskResult(taskId, true);

        byte[] expected =
                ByteBuffer.allocate(Codec.getSerializedSize(msg))
                        .put(MESSAGE_BEGIN)
                        .put(msg.getType().getCode())
                        .putLong(taskId.getMostSignificantBits())
                        .putLong(taskId.getLeastSignificantBits())
                        .put((byte) 1)
                        .array();

        byte[] actual = Codec.serialize(VER, msg);
        Assertions.assertArrayEquals(expected, actual);

        Message deserialized = Codec.deserialize(actual, 0, actual.length);
        Assertions.assertEquals(msg, deserialized);
    }

    @Test
    void testStop() throws Exception {
        UUID taskId = new UUID(7L, 8L);

        Message.TaskStop msg = new Message.TaskStop(taskId);

        byte[] expected =
                ByteBuffer.allocate(Codec.getSerializedSize(msg))
                        .put(MESSAGE_BEGIN)
                        .put(msg.getType().getCode())
                        .putLong(taskId.getMostSignificantBits())
                        .putLong(taskId.getLeastSignificantBits())
                        .array();

        byte[] actual = Codec.serialize(VER, msg);
        Assertions.assertArrayEquals(expected, actual);

        Message deserialized = Codec.deserialize(actual, 0, actual.length);
        Assertions.assertEquals(msg, deserialized);
    }

    @Test
    void testFailed() throws Exception {
        UUID taskId = new UUID(9L, 10L);

        Message.TaskFailed msg = new Message.TaskFailed(taskId);

        byte[] expected =
                ByteBuffer.allocate(Codec.getSerializedSize(msg))
                        .put(MESSAGE_BEGIN)
                        .put(msg.getType().getCode())
                        .putLong(taskId.getMostSignificantBits())
                        .putLong(taskId.getLeastSignificantBits())
                        .array();

        byte[] actual = Codec.serialize(VER, msg);
        Assertions.assertArrayEquals(expected, actual);

        Message deserialized = Codec.deserialize(actual, 0, actual.length);
        Assertions.assertEquals(msg, deserialized);
    }

    @Test
    void testPing() throws Exception {
        Message.Ping msg = new Message.Ping();

        byte[] expected =
                ByteBuffer.allocate(Codec.getSerializedSize(msg))
                        .put(MESSAGE_BEGIN)
                        .put(msg.getType().getCode())
                        .array();

        byte[] actual = Codec.serialize(VER, msg);
        Assertions.assertArrayEquals(expected, actual);

        Message deserialized = Codec.deserialize(actual, 0, actual.length);
        Assertions.assertEquals(msg, deserialized);
    }

    @Test
    void testPong() throws Exception {
        Message.Pong msg = new Message.Pong();

        byte[] expected =
                ByteBuffer.allocate(Codec.getSerializedSize(msg))
                        .put(MESSAGE_BEGIN)
                        .put(msg.getType().getCode())
                        .array();

        byte[] actual = Codec.serialize(VER, msg);
        Assertions.assertArrayEquals(expected, actual);

        Message deserialized = Codec.deserialize(actual, 0, actual.length);
        Assertions.assertEquals(msg, deserialized);
    }

    @Test
    void testHandshake() throws Exception {
        UUID nodeId = new UUID(11L, 12L);

        Message.Handshake msg = new Message.Handshake(nodeId);

        byte[] expected =
                ByteBuffer.allocate(Codec.getSerializedSize(msg))
                        .put(MESSAGE_BEGIN)
                        .put(msg.getType().getCode())
                        .putLong(nodeId.getMostSignificantBits())
                        .putLong(nodeId.getLeastSignificantBits())
                        .array();

        byte[] actual = Codec.serialize(VER, msg);
        Assertions.assertArrayEquals(expected, actual);

        Message deserialized = Codec.deserialize(actual, 0, actual.length);
        Assertions.assertEquals(msg, deserialized);
    }

    @Test
    void testWrongMagicNumberException() {
        byte[] badMagicData = new byte[] {(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};

        Assertions.assertThrows(
                WrongMagicNumberException.class,
                () -> {
                    Codec.deserialize(badMagicData, 0, badMagicData.length);
                });
    }

    @Test
    void testUnknownMessageTypeException() {
        byte[] badTypeData = ByteBuffer.allocate(6).put(MESSAGE_BEGIN).put((byte) 0xFF).array();

        Assertions.assertThrows(
                UnknownMessageTypeException.class,
                () -> {
                    Codec.deserialize(badTypeData, 0, badTypeData.length);
                });
    }

    @Test
    void testUnknownVersionException() {
        byte[] badVersionData =
                ByteBuffer.allocate(6)
                        .put((byte) 80)
                        .put((byte) 78)
                        .put((byte) 80)
                        .putShort((short) 9999)
                        .put((byte) 1)
                        .array();

        Assertions.assertThrows(
                UnknownVersionException.class,
                () -> {
                    Codec.deserialize(badVersionData, 0, badVersionData.length);
                });
    }
}
