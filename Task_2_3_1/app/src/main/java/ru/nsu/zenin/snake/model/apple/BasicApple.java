package ru.nsu.zenin.snake.model.apple;

import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.Snake;

public class BasicApple extends Apple {
    public BasicApple(Point2D position) {
        super(position);
    }

    public void apply(Snake snake) {
        snake.grow();
    }
}
