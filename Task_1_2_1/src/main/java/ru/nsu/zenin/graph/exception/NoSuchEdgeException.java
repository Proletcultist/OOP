package ru.nsu.zenin.graph.exception;

public class NoSuchEdgeException extends Exception {
    public NoSuchEdgeException(String msg) {
        super(msg);
    }

    public NoSuchEdgeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
