package ru.nsu.zenin.primenumbers.cluster.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.UUID;
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
        ret += Short.BYTES;
        ret += Byte.BYTES;

        ret +=
                switch (msg) {
                    case Message.Presence p -> Long.BYTES + Long.BYTES + 4 * Byte.BYTES + Integer.BYTES;
                    case Message.TaskSubmit t ->
                            Long.BYTES
                                    + Long.BYTES
                                    + Integer.BYTES
                                    + t.numbers().length * Integer.BYTES;
                    case Message.TaskResult r -> Long.BYTES + Long.BYTES + Byte.BYTES;
                    case Message.TaskStop s -> Long.BYTES + Long.BYTES;
                    case Message.TaskFailed s -> Long.BYTES + Long.BYTES;
                    case Message.Ping p -> 0;
                    case Message.Pong p -> 0;
                    case Message.Handshake h -> Long.BYTES + Long.BYTES;
                };

        return ret;
    }

    public static void serialize(DataOutputStream dos, ProtocolVersion ver, Message msg)
            throws IOException {
        dos.write(MAGIC_NUMBER, 0, MAGIC_NUMBER.length);
        dos.writeShort(ver.getVersionCode());
        dos.writeByte(msg.getType().getCode());

        switch (msg) {
            case Message.Presence p -> {
                dos.writeLong(p.nodeId().getMostSignificantBits());
                dos.writeLong(p.nodeId().getLeastSignificantBits());
                dos.write(p.address().getAddress(), 0, 4);
                dos.writeInt(p.port());
            }
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
            case Message.TaskFailed f -> {
                dos.writeLong(f.taskId().getMostSignificantBits());
                dos.writeLong(f.taskId().getLeastSignificantBits());
            }
            case Message.Ping p -> {}
            case Message.Pong p -> {}
            case Message.Handshake h -> {
                dos.writeLong(h.nodeId().getMostSignificantBits());
                dos.writeLong(h.nodeId().getLeastSignificantBits());
            }
        }
    }

    public static Message deserialize(DataInputStream dis)
            throws UnknownMessageTypeException,
                    UnknownVersionException,
                    WrongMagicNumberException,
                    IOException,
                    EOFException {

        byte[] magic_num = new byte[MAGIC_NUMBER.length];
        dis.readFully(magic_num);
        if (!Arrays.equals(magic_num, MAGIC_NUMBER)) {
            throw new WrongMagicNumberException(
                    "Wrong magic number: " + Arrays.toString(magic_num));
        }

        ProtocolVersion ver = ProtocolVersion.fromCode(dis.readShort());
        MessageType type = MessageType.fromCode(dis.readByte());

        return switch (type) {
            case PRESENCE -> {
                UUID id = new UUID(dis.readLong(), dis.readLong());
                byte[] ip = new byte[4];
                dis.readFully(ip);
                int port = dis.readInt();
                yield new Message.Presence(id, InetAddress.getByAddress(ip), port);
            }
            case TASK_SUBMIT -> {
                UUID id = new UUID(dis.readLong(), dis.readLong());
                int[] nums = new int[dis.readInt()];
                for (int i = 0; i < nums.length; i++) nums[i] = dis.readInt();
                yield new Message.TaskSubmit(id, nums);
            }
            case TASK_RESULT ->
                    new Message.TaskResult(
                            new UUID(dis.readLong(), dis.readLong()), dis.readBoolean());
            case TASK_STOP -> new Message.TaskStop(new UUID(dis.readLong(), dis.readLong()));
            case TASK_FAILED -> new Message.TaskFailed(new UUID(dis.readLong(), dis.readLong()));
            case PING -> new Message.Ping();
            case PONG -> new Message.Pong();
            case HANDSHAKE -> new Message.Handshake(new UUID(dis.readLong(), dis.readLong()));
        };
    }
}
