package ru.nsu.zenin.primenumbers.protocol.exception;

public class WrongMagicNumberException extends Exception {
    public WrongMagicNumberException(String msg) {
        super(msg);
    }
}
