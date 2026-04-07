package ru.nsu.zenin.snake.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.ArrayList;
import ru.nsu.zenin.collection.Point2D;

public class ObservableSnake implements Snake {
    private final Deque<Point2D> segments;
    private Integer targetSize;

    private Direction lastMoveDirection;
    private Direction pendingDirection;

    private Integer ticksToMove;
    private Integer counter = 0;

    List<SnakeChangeListener> listeners;

    public ObservableSnake(Point2D head, Direction direction, Integer ticksToMove) {
        listeners = new ArrayList<SnakeChangeListener>();
        segments = new ArrayDeque<Point2D>();
        segments.addFirst(head);
        targetSize = 1;
        this.pendingDirection = direction;
        this.lastMoveDirection = direction;
        this.ticksToMove = ticksToMove;
    }

    public Direction getLastMoveDirection() {
        return lastMoveDirection;
    }

    public void setPendingDirection(Direction direction) {
        this.pendingDirection = direction;
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
            int sizeOnTickStart = segments.size();
            int targetSizeOnTickStart = targetSize;

            Point2D currHead = segments.getFirst();
            Point2D nextHead =
                    switch (this.pendingDirection) {
                        case UP -> new Point2D(currHead.x(), currHead.y() - 1);
                        case DOWN -> new Point2D(currHead.x(), currHead.y() + 1);
                        case RIGHT -> new Point2D(currHead.x() + 1, currHead.y());
                        case LEFT -> new Point2D(currHead.x() - 1, currHead.y());
                    };
            segments.addFirst(nextHead);

            for (SnakeChangeListener listener : listeners) {
                listener.onChange(new SnakeChangeListener.Change.HeadMovedTo(nextHead, currHead));
            }

            if (sizeOnTickStart == targetSizeOnTickStart) {
                popTail();
            } else if (sizeOnTickStart > targetSizeOnTickStart) {
                popTail();
                popTail();
            }

            counter = 0;
            lastMoveDirection = pendingDirection;
        } else {
            counter++;
        }
    }

    private void popTail() {
        Point2D removed = segments.removeLast();
        Point2D newTail = segments.getLast();
        for (SnakeChangeListener listener : listeners) {
            listener.onChange(new SnakeChangeListener.Change.TailMovedFrom(removed, newTail));
        }
    }

    public void addListener(SnakeChangeListener listener) {
        listeners.add(listener);
    }
}
