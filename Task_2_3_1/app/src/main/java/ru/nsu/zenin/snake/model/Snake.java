package ru.nsu.zenin.snake.model;

import java.util.Deque;
import ru.nsu.zenin.collection.Point2D;

public interface Snake {
    Direction getLastMoveDirection();

    void setPendingDirection(Direction direction);

    void setTicksToMove(Integer ticksToMove);

    Integer getTicksToMove();

    Deque<Point2D> getSegments();

    Point2D getHead();

    void grow();

    void shrink();

    void tick();

    int size();

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        public Direction getOpposite() {
            return switch (this) {
                case UP -> DOWN;
                case DOWN -> UP;
                case LEFT -> RIGHT;
                case RIGHT -> LEFT;
            };
        }
    }
}
