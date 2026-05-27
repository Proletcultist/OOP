package ru.nsu.zenin.primenumbers.logging.exception;

public class IllegalLoggerStateException extends RuntimeException {
    public IllegalLoggerStateException(String msg) {
        super(msg);
    }
}
