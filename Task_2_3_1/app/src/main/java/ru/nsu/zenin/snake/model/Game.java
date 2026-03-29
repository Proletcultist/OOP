package ru.nsu.zenin.snake.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ru.nsu.zenin.collection.Point2D;

public class Game {
    private Set<Point2D> available = new HashSet<Point2D>();

    private final List<Snake> snakes = new ArrayList<Snake>();
    private AppleFactory appleFactory = null;

    private Integer tickNum = 0;

    public Game(int width, int height) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                available.add(new Point2D(i, j));
            }
        }
    }

    public void addSnake(Snake snake) {
        snakes.add(snake);
        available.remove(snake.getHead());
    }

    public void setAppleFactory(AppleFactory factory) {
        this.appleFactory = factory;
    }

    void occupy(Point2D point) {
        available.remove(point);
    }

    void free(Point2D point) {
        available.add(point);
    }

    public void tick() {
        Integer maxTicksToMove = 1;
        for (Snake snake : snakes) {
            if (tickNum % snake.getTicksToMove() == 0) {
                snake.move();
            }
            if (snake.getTicksToMove() > maxTicksToMove) {
                maxTicksToMove = snake.getTicksToMove();
            }
        }
        tickNum = (tickNum + 1) % maxTicksToMove;
    }
}
