package ru.nsu.zenin.logging.exception;

public class IllegalLoggerStateException extends RuntimeException {
    public IllegalLoggerStateException(String msg) {
        super(msg);
    }
}
