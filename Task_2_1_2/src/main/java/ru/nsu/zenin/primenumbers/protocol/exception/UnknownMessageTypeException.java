package ru.nsu.zenin.primenumbers.protocol.exception;

public class UnknownMessageTypeException extends Exception {
    public UnknownMessageTypeException(String msg) {
        super(msg);
    }
}
