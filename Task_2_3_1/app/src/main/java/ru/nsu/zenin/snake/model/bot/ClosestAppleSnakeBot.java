package ru.nsu.zenin.snake.model.bot;

import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.collection.Field;
import ru.nsu.zenin.snake.model.Snake;
import ru.nsu.zenin.snake.model.Game;
import ru.nsu.zenin.snake.model.TileState;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayDeque;

public class ClosestAppleSnakeBot extends SnakeBot {
    private Point2D target;
    
    public ClosestAppleSnakeBot(Snake snake, Field<TileState> field) {
        super(snake, field);
        findClosest();
    }
    
    public void tick() {
        if (!(field.get(target) instanceof TileState.OccupiedByApple)) {
            findClosest();
        }
        
        Point2D head = snake.getHead();
        
        Snake.Direction nextMove = calculateNextMove(head, target);
        
        if (nextMove != null) {
            snake.setPendingDirection(nextMove);
        }
    }
    
    private Snake.Direction calculateNextMove(Point2D head, Point2D target) {
        int dx = target.x() - head.x();
        int dy = target.y() - head.y();
        
        if (dx != 0) {
            Snake.Direction horizontalMove = dx > 0 ? Snake.Direction.RIGHT : Snake.Direction.LEFT;
            Point2D nextPos = new Point2D(head.x() + (dx > 0 ? 1 : -1), head.y());
            
            if (isSafeMove(nextPos)) {
                return horizontalMove;
            }
        }
        
        if (dy != 0) {
            Snake.Direction verticalMove = dy > 0 ? Snake.Direction.DOWN : Snake.Direction.UP;
            Point2D nextPos = new Point2D(head.x(), head.y() + (dy > 0 ? 1 : -1));
            
            if (isSafeMove(nextPos)) {
                return verticalMove;
            }
        }
        
        return findAlternativeDirection(head);
    }
    
    private boolean isSafeMove(Point2D nextPos) {
        return field.contains(nextPos) && 
               (field.get(nextPos) instanceof TileState.Free || field.get(nextPos) instanceof TileState.OccupiedByApple);
    }
    
    private Snake.Direction findAlternativeDirection(Point2D head) {
        Snake.Direction[] directions = {
            Snake.Direction.UP, Snake.Direction.DOWN, 
            Snake.Direction.LEFT, Snake.Direction.RIGHT
        };
        
        for (Snake.Direction dir : directions) {
            Point2D nextPos = getNextPosition(head, dir);
            if (isSafeMove(nextPos)) {
                return dir;
            }
        }
        
        return null;
    }
    
    private Point2D getNextPosition(Point2D current, Snake.Direction direction) {
        switch (direction) {
            case UP: return new Point2D(current.x(), current.y() - 1);
            case DOWN: return new Point2D(current.x(), current.y() + 1);
            case LEFT: return new Point2D(current.x() - 1, current.y());
            case RIGHT: return new Point2D(current.x() + 1, current.y());
            default: return current;
        }
    }
    
    private void findClosest() {
        Queue<Point2D> pending = new ArrayDeque<>();
        Set<Point2D> visited = new HashSet<>();
        
        pending.add(snake.getHead());
        visited.add(snake.getHead());
        
        while (!pending.isEmpty()) {
            Point2D taken = pending.remove();
            
            Point2D left = new Point2D(taken.x() - 1, taken.y());
            Point2D right = new Point2D(taken.x() + 1, taken.y());
            Point2D up = new Point2D(taken.x(), taken.y() - 1);
            Point2D down = new Point2D(taken.x(), taken.y() + 1);
            
            Point2D[] neighbours = {left, right, up, down};
            
            for (Point2D n : neighbours) {
                if (field.contains(n) && !visited.contains(n)) {
                    if (field.get(n) instanceof TileState.OccupiedByApple) {
                        target = n;
                        return;
                    }
                    if (field.get(n) instanceof TileState.Free) {
                        pending.add(n);
                        visited.add(n);
                    }
                }
            }
        }
    }
}
