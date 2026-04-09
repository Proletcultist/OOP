package ru.nsu.zenin.snake.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
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

            Point2D prevHead = segments.getFirst();
            Point2D nextHead =
                    switch (this.pendingDirection) {
                        case UP -> new Point2D(prevHead.x(), prevHead.y() - 1);
                        case DOWN -> new Point2D(prevHead.x(), prevHead.y() + 1);
                        case RIGHT -> new Point2D(prevHead.x() + 1, prevHead.y());
                        case LEFT -> new Point2D(prevHead.x() - 1, prevHead.y());
                    };

            segments.addFirst(nextHead);

            Point2D prevTail = segments.removeLast();
            Point2D nextTail = segments.getLast();

            sendChange(
                    new SnakeChangeListener.Change.Moved(nextHead, prevHead, nextTail, prevTail));

            if (sizeOnTickStart < targetSizeOnTickStart) {
                segments.addLast(prevTail);
                sendChange(new SnakeChangeListener.Change.Growed(prevTail, nextTail));
            } else if (sizeOnTickStart > targetSizeOnTickStart) {
                prevTail = segments.removeLast();
                nextTail = segments.getLast();
                sendChange(new SnakeChangeListener.Change.Shrinked(nextTail, prevTail));
            }

            counter = 0;
            lastMoveDirection = pendingDirection;
        } else {
            counter++;
        }
    }

    private void sendChange(SnakeChangeListener.Change c) {
        for (SnakeChangeListener l : listeners) {
            l.onChange(c);
        }
    }

    public int size() {
        return segments.size();
    }

    public void addListener(SnakeChangeListener listener) {
        listeners.add(listener);
    }
}
