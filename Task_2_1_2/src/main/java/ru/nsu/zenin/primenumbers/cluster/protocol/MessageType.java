package ru.nsu.zenin.primenumbers.cluster.protocol;

import lombok.Getter;
import ru.nsu.zenin.primenumbers.cluster.protocol.exception.UnknownMessageTypeException;

public enum MessageType {
    PRESENCE((byte) 0x01),
    TASK_SUBMIT((byte) 0x02),
    TASK_RESULT((byte) 0x03),
    TASK_STOP((byte) 0x04),
    PING((byte) 0x05),
    PONG((byte) 0x06);

    @Getter private final byte code;

    MessageType(byte code) {
        this.code = code;
    }

    public static MessageType fromCode(byte code) throws UnknownMessageTypeException {
        return switch (code) {
            case 0x01 -> PRESENCE;
            case 0x02 -> TASK_SUBMIT;
            case 0x03 -> TASK_RESULT;
            case 0x04 -> TASK_STOP;
            case 0x05 -> PING;
            case 0x06 -> PONG;
            default -> throw new UnknownMessageTypeException("Unknown message type: " + code);
        };
    }
}
