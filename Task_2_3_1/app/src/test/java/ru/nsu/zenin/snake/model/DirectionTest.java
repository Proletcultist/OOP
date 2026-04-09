package ru.nsu.zenin.snake.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class DirectionTest {
    @ParameterizedTest
    @EnumSource(Snake.Direction.class)
    void tickTest(Snake.Direction dir) {
        switch (dir) {
            case Snake.Direction.UP ->
                    Assertions.assertEquals(dir.getOpposite(), Snake.Direction.DOWN);
            case Snake.Direction.DOWN ->
                    Assertions.assertEquals(dir.getOpposite(), Snake.Direction.UP);
            case Snake.Direction.LEFT ->
                    Assertions.assertEquals(dir.getOpposite(), Snake.Direction.RIGHT);
            case Snake.Direction.RIGHT ->
                    Assertions.assertEquals(dir.getOpposite(), Snake.Direction.LEFT);
        }
    }
}
