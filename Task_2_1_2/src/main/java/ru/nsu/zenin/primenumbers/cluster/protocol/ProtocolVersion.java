package ru.nsu.zenin.primenumbers.cluster.protocol;

import lombok.Getter;
import ru.nsu.zenin.primenumbers.cluster.protocol.exception.UnknownVersionException;

public enum ProtocolVersion {
    LEET_VER((short) 1337);

    @Getter private final short versionCode;

    ProtocolVersion(short versionCode) {
        this.versionCode = versionCode;
    }

    public static ProtocolVersion fromCode(short ver) throws UnknownVersionException {
        return switch (ver) {
            case 1337 -> LEET_VER;
            default -> throw new UnknownVersionException("Unknown protocol version type: " + ver);
        };
    }
}
