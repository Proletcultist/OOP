package ru.nsu.zenin.snake.model;

import java.util.ArrayList;
import java.util.List;
import ru.nsu.zenin.collection.Point2D;

public class Snake {
    private final List<Point2D> segments;
    private Integer targetSize;
    private Direction direction;

    private Integer ticksToMove;
    private Integer counter = 0;
    private Integer score = 0;

    public Snake(Point2D head, Direction direction, Integer ticksToMove) {
        segments = new ArrayList<Point2D>();
        segments.add(head);
        targetSize = 1;
        this.direction = direction;
        this.ticksToMove = ticksToMove;
    }

    public Snake(List<Point2D> segments, Direction direction, Integer ticksToMove) {
        if (segments.isEmpty()) {
            // TODO: Throw
        }
        this.segments = segments;
        this.targetSize = segments.size();
        this.direction = direction;
        this.ticksToMove = ticksToMove;
    }

    public void changeDirection(Direction direction) {
        if (this.direction.getOpposite() == direction) {
            // TODO: Throw
        }
        this.direction = direction;
    }

    public void setTicksToMove(Integer ticksToMove) {
        this.ticksToMove = ticksToMove;
    }

    public Integer getTicksToMove() {
        return ticksToMove;
    }

    public void grow() {
        targetSize += 1;
    }

    public void shrink() {
        if (targetSize > 1) {
            targetSize -= 1;
        }
    }

    public void tick() {
        if (counter >= ticksToMove) {
            Point2D currHead = segments.get(0);

            if (segments.size() == targetSize) {
                segments.remove(segments.size() - 1);
            } else if (segments.size() > targetSize) {
                segments.remove(segments.size() - 1);
                segments.remove(segments.size() - 1);
            }

            Point2D nextHead =
                    switch (this.direction) {
                        case UP -> new Point2D(currHead.x(), currHead.y() - 1);
                        case DOWN -> new Point2D(currHead.x(), currHead.y() + 1);
                        case RIGHT -> new Point2D(currHead.x() + 1, currHead.y());
                        case LEFT -> new Point2D(currHead.x() - 1, currHead.y());
                    };

            segments.add(0, nextHead);

            counter = 0;
        } else {
            counter++;
        }
    }

    public Point2D getHead() {
        return segments.get(0);
    }

    public List<Point2D> getSegments() {
        return segments;
    }

    public void addPoints(int points) {
        score += points;
    }

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
