package ru.nsu.zenin.snake.model.apple;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.ObservableSnake;
import ru.nsu.zenin.snake.model.Snake;

public class ShrinkingAppleTest {
    @Test
    void shrinkTest() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        snake.grow();
        snake.tick();

        Apple apple = new ShrinkingApple(new Point2D(0, 0));
        apple.apply(snake);
        snake.tick();

        Point2D[] expect = {new Point2D(2, 0)};
        Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect));
    }
}
