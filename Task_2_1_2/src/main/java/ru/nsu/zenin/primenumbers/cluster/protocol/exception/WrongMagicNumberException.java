package ru.nsu.zenin.primenumbers.cluster.protocol.exception;

public class WrongMagicNumberException extends Exception {
    public WrongMagicNumberException(String msg) {
        super(msg);
    }
}
