package ru.nsu.zenin.primenumbers.protocol;

import lombok.Getter;
import ru.nsu.zenin.primenumbers.protocol.exception.UnknownMessageTypeException;

public enum MessageType {
    DISCOVER((byte) 0x01),
    PRESENCE((byte) 0x02),
    TASK_SUBMIT((byte) 0x03),
    TASK_RESULT((byte) 0x04),
    TASK_STOP((byte) 0x05),
    PING((byte) 0x06),
    PONG((byte) 0x07);

    @Getter private final byte code;

    MessageType(byte code) {
        this.code = code;
    }

    public static MessageType fromCode(byte code) throws UnknownMessageTypeException {
        return switch (code) {
            case 0x01 -> DISCOVER;
            case 0x02 -> PRESENCE;
            case 0x03 -> TASK_SUBMIT;
            case 0x04 -> TASK_RESULT;
            case 0x05 -> TASK_STOP;
            case 0x06 -> PING;
            case 0x07 -> PONG;
            default -> throw new UnknownMessageTypeException("Unknown message type: " + code);
        };
    }
}
