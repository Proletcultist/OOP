package ru.nsu.zenin.snake.model;

public class NoAvailablePointsException extends RuntimeException {
    public NoAvailablePointsException(String msg) {
        super(msg);
    }
}
