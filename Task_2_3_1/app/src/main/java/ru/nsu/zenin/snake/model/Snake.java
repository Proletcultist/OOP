package ru.nsu.zenin.snake.model;

import java.util.ArrayDeque;
import java.util.Deque;
import ru.nsu.zenin.collection.Point2D;

public class Snake {
    private final Deque<Point2D> segments;
    private Integer targetSize;
    private Direction direction;

    private Integer ticksToMove;
    private Integer counter = 0;
    private Integer score = 0;

    public Snake(Point2D head, Direction direction, Integer ticksToMove) {
        segments = new ArrayDeque<Point2D>();
        segments.addFirst(head);
        targetSize = 1;
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

    public void tick(GameField field) {
        if (counter >= ticksToMove) {
            if (segments.size() == targetSize) {
                field.setTileState(segments.pop(), new GameField.TileState.Free());
            } else if (segments.size() > targetSize) {
                field.setTileState(segments.pop(), new GameField.TileState.Free());
                field.setTileState(segments.pop(), new GameField.TileState.Free());
            }

            Point2D currHead = segments.getFirst();
            Point2D nextHead =
                    switch (this.direction) {
                        case UP -> new Point2D(currHead.x(), currHead.y() - 1);
                        case DOWN -> new Point2D(currHead.x(), currHead.y() + 1);
                        case RIGHT -> new Point2D(currHead.x() + 1, currHead.y());
                        case LEFT -> new Point2D(currHead.x() - 1, currHead.y());
                    };
            segments.addFirst(nextHead);
            field.setTileState(nextHead, new GameField.TileState.OccupiedBySnake(this));

            counter = 0;
        } else {
            counter++;
        }
    }

    public Point2D getHead() {
        return segments.getFirst();
    }

    public Deque<Point2D> getSegments() {
        return segments;
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
