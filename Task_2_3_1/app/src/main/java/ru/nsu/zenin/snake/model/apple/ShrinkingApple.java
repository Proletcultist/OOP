package ru.nsu.zenin.snake.model.apple;

import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.Snake;

public class ShrinkingApple extends Apple {
    public ShrinkingApple(Point2D position) {
        super(position);
    }

    public void apply(Snake snake) {
        snake.shrink();
    }
}
