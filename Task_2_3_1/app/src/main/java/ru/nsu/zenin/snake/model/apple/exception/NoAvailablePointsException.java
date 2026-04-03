package ru.nsu.zenin.snake.model.apple.exception;

public class NoAvailablePointsException extends RuntimeException {
    public NoAvailablePointsException(String msg) {
        super(msg);
    }
}
