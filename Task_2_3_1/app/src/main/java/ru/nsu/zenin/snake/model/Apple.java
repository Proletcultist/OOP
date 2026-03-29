package ru.nsu.zenin.snake.model;

import ru.nsu.zenin.collection.Point2D;

public abstract class Apple {
    private final Point2D position;

    public Apple(Point2D position) {
        this.position = position;
    }

    public Point2D getPosition() {
        return position;
    }

    public abstract void apply(Snake snake);
}
