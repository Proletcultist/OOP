package ru.nsu.zenin.snake.view;

import ru.nsu.zenin.collection.Point2D;

@FunctionalInterface
public interface SnakeChangeListener {
    void onChange(Change c);

    public record Change(ObservableSnake snake, Point2D point, Action act) {
        public enum Action {
            OCCUPIED,
            LEFT
        }
    }
}
