package ru.nsu.zenin.primenumbers.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import ru.nsu.zenin.primenumbers.protocol.exception.UnknownMessageTypeException;
import ru.nsu.zenin.primenumbers.protocol.exception.UnknownVersionException;
import ru.nsu.zenin.primenumbers.protocol.exception.WrongMagicNumberException;

public class Codec {
    private static final String MAGIC_NUMBER = "PNP";

    private Codec() {}

    public static byte[] serialize(ProtocolVersion ver, Message msg) {
        ByteArrayOutputStream payloadBaos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(payloadBaos)) {
            dos.writeChars(MAGIC_NUMBER);
            dos.writeShort(ver.getVersionCode());
            dos.writeByte(msg.getType().getCode());

            writeMessagePayload(dos, msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] payloadBytes = payloadBaos.toByteArray();

        ByteArrayOutputStream frameBaos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(frameBaos)) {
            dos.writeInt(payloadBytes.length);
            dos.write(payloadBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return frameBaos.toByteArray();
    }

    public static Message deserialize(byte[] data)
            throws UnknownMessageTypeException,
                    UnknownVersionException,
                    WrongMagicNumberException,
                    IOException,
                    EOFException {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {
            String magic_num = readChars(dis, MAGIC_NUMBER.length());
            if (!magic_num.equals(MAGIC_NUMBER)) {
                throw new WrongMagicNumberException("Wrong magic number: " + magic_num);
            }

            ProtocolVersion ver = ProtocolVersion.fromCode(dis.readShort());
            MessageType type = MessageType.fromCode(dis.readByte());

            return switch (type) {
                case DISCOVER -> new Message.Discover();
                case PRESENCE -> new Message.Presence(dis.readInt());
                case TASK_SUBMIT -> {
                    java.util.UUID id = new java.util.UUID(dis.readLong(), dis.readLong());
                    int[] nums = new int[dis.readInt()];
                    for (int i = 0; i < nums.length; i++) nums[i] = dis.readInt();
                    yield new Message.TaskSubmit(id, nums);
                }
                case TASK_RESULT ->
                        new Message.TaskResult(
                                new java.util.UUID(dis.readLong(), dis.readLong()),
                                dis.readBoolean());
                case TASK_STOP ->
                        new Message.TaskStop(new java.util.UUID(dis.readLong(), dis.readLong()));
                case PING -> new Message.Ping();
                case PONG -> new Message.Pong();
            };
        }
    }

    private static void writeMessagePayload(DataOutputStream dos, Message msg) throws IOException {
        switch (msg) {
            case Message.Discover d -> {}
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

    private static String readChars(DataInputStream dis, int count)
            throws IOException, EOFException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(dis.readChar());
        }
        return sb.toString();
    }
}
