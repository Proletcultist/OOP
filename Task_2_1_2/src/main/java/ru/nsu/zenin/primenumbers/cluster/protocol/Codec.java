package ru.nsu.zenin.primenumbers.cluster.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import ru.nsu.zenin.primenumbers.cluster.protocol.exception.UnknownMessageTypeException;
import ru.nsu.zenin.primenumbers.cluster.protocol.exception.UnknownVersionException;
import ru.nsu.zenin.primenumbers.cluster.protocol.exception.WrongMagicNumberException;

public class Codec {
    private static final byte[] MAGIC_NUMBER = {(byte) 80, (byte) 78, (byte) 80};

    private Codec() {}

    public static byte[] serialize(ProtocolVersion ver, Message msg) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(Codec.getSerializedSize(msg))) {
            try (DataOutputStream dos = new DataOutputStream(baos)) {
                Codec.serialize(dos, ver, msg);
            }

            return baos.toByteArray();
        }
    }

    public static Message deserialize(byte[] data, int offset, int length)
            throws UnknownMessageTypeException,
                    UnknownVersionException,
                    WrongMagicNumberException,
                    IOException,
                    EOFException {
        try (DataInputStream dis =
                new DataInputStream(new ByteArrayInputStream(data, offset, length))) {
            return Codec.deserialize(dis);
        }
    }

    public static int getSerializedSize(Message msg) {
        int ret = 0;

        ret += MAGIC_NUMBER.length;
        ret += Short.SIZE;
        ret += Byte.SIZE;

        ret +=
                switch (msg) {
                    case Message.Discover d -> Integer.SIZE;
                    case Message.Presence p -> Integer.SIZE;
                    case Message.TaskSubmit t ->
                            Long.SIZE
                                    + Long.SIZE
                                    + Integer.SIZE
                                    + t.numbers().length * Integer.SIZE;
                    case Message.TaskResult r -> Long.SIZE + Long.SIZE + Byte.SIZE;
                    case Message.TaskStop s -> Long.SIZE + Long.SIZE;
                    case Message.Ping p -> 0;
                    case Message.Pong p -> 0;
                };

        return ret;
    }

    public static void serialize(DataOutputStream dos, ProtocolVersion ver, Message msg)
            throws IOException {
        dos.write(MAGIC_NUMBER, 0, MAGIC_NUMBER.length);
        dos.writeShort(ver.getVersionCode());
        dos.writeByte(msg.getType().getCode());

        switch (msg) {
            case Message.Discover d -> dos.writeInt(d.tcpPort());
            case Message.Presence p -> dos.writeInt(p.tcpPort());
            case Message.TaskSubmit t -> {
                dos.writeLong(t.taskId().getMostSignificantBits());
                dos.writeLong(t.taskId().getLeastSignificantBits());
                dos.writeInt(t.numbers().length);
                for (int num : t.numbers()) dos.writeInt(num);
            }
            case Message.TaskResult r -> {
                dos.writeLong(r.taskId().getMostSignificantBits());
                dos.writeLong(r.taskId().getLeastSignificantBits());
                dos.writeBoolean(r.hasComposite());
            }
            case Message.TaskStop s -> {
                dos.writeLong(s.taskId().getMostSignificantBits());
                dos.writeLong(s.taskId().getLeastSignificantBits());
            }
            case Message.Ping p -> {}
            case Message.Pong p -> {}
        }
    }

    public static Message deserialize(DataInputStream dis)
            throws UnknownMessageTypeException,
                    UnknownVersionException,
                    WrongMagicNumberException,
                    IOException,
                    EOFException {

        byte[] magic_num = new byte[MAGIC_NUMBER.length];
        if (dis.read(magic_num, 0, magic_num.length) != MAGIC_NUMBER.length) {
            throw new EOFException("Unexpected EOF");
        }
        if (!Arrays.equals(magic_num, MAGIC_NUMBER)) {
            throw new WrongMagicNumberException(
                    "Wrong magic number: " + Arrays.toString(magic_num));
        }

        ProtocolVersion ver = ProtocolVersion.fromCode(dis.readShort());
        MessageType type = MessageType.fromCode(dis.readByte());

        return switch (type) {
            case DISCOVER -> new Message.Discover(dis.readInt());
            case PRESENCE -> new Message.Presence(dis.readInt());
            case TASK_SUBMIT -> {
                java.util.UUID id = new java.util.UUID(dis.readLong(), dis.readLong());
                int[] nums = new int[dis.readInt()];
                for (int i = 0; i < nums.length; i++) nums[i] = dis.readInt();
                yield new Message.TaskSubmit(id, nums);
            }
            case TASK_RESULT ->
                    new Message.TaskResult(
                            new java.util.UUID(dis.readLong(), dis.readLong()), dis.readBoolean());
            case TASK_STOP ->
                    new Message.TaskStop(new java.util.UUID(dis.readLong(), dis.readLong()));
            case PING -> new Message.Ping();
            case PONG -> new Message.Pong();
        };
    }
}
