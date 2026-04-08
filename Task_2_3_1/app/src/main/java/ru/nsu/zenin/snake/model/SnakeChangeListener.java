package ru.nsu.zenin.snake.model;

import ru.nsu.zenin.collection.Point2D;

@FunctionalInterface
public interface SnakeChangeListener {
    void onChange(Change c);

    public sealed interface Change {
        public record Moved(Point2D newHead, Point2D prevHead, Point2D newTail, Point2D prevTail) implements Change {}
        public record Shrinked(Point2D newTail, Point2D prevTail) implements Change {}
        public record Growed(Point2D newTail, Point2D prevTail) implements Change {}
    }
}
