package ru.nsu.zenin.snake.model;

import ru.nsu.zenin.collection.Point2D;

@FunctionalInterface
public interface SnakeChangeListener {
    void onChange(Change c);

    public sealed interface Change {
        public record HeadMovedTo(Point2D head, Point2D prevHead) implements Change {}
        public record TailMovedFrom(Point2D tail, Point2D newTail) implements Change {}
    }
}
