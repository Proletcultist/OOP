package ru.nsu.zenin.snake.model.apple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.ObservableSnake;
import ru.nsu.zenin.snake.model.Snake;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class BasicAppleTest {
    @Test
    void growTest() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);

        Apple apple = new BasicApple(new Point2D(0, 0));

        apple.apply(snake);

        snake.tick();

        Point2D[] expect = {new Point2D(1, 0), new Point2D(0, 0)};
        Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect));
    }
}
