package ru.nsu.zenin.snake.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ru.nsu.zenin.collection.Point2D;

public class Game {
    private final GameField field;
    private AppleFactory appleFactory = null;
    private final List<Snake> snakes = new ArrayList<Snake>();

    public Game(GameField field) {
        this.field = field;
    }

    public void addSnake(Snake snake) {
        snakes.add(snake);

        for (Point2D p : snake.getSegments()) {
            field.setTileState(p, new GameField.TileState.OccupiedBySnake(snake));
        }
    }

    public void setAppleFactory(AppleFactory factory) {
        this.appleFactory = factory;
    }

    public void tick() {
        for (Snake snake : snakes) {
            snake.tick(field);
        }
    }
}
