package ru.nsu.zenin.graph.exception;

public class NoSuchVertexException extends Exception {
    public NoSuchVertexException(String msg) {
        super(msg);
    }

    public NoSuchVertexException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
