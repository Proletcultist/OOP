package ru.nsu.zenin.tester.service.logging.exception;

public class IllegalLoggerStateException extends RuntimeException {
    public IllegalLoggerStateException(String msg) {
        super(msg);
    }
}
