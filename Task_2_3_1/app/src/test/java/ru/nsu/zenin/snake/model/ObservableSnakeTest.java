package ru.nsu.zenin.snake.model;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.nsu.zenin.collection.Point2D;

public class ObservableSnakeTest {
    @Test
    void constructorTest() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 1);
        Point2D[] expect = {new Point2D(0, 0)};

        Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect));
    }

    @ParameterizedTest
    @EnumSource(Snake.Direction.class)
    void tickTest(Snake.Direction dir) {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), dir, 0);
        snake.tick();

        switch (dir) {
            case Snake.Direction.UP -> {
                Point2D[] expect = {new Point2D(0, -1)};
                Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect));
            }
            case Snake.Direction.DOWN -> {
                Point2D[] expect = {new Point2D(0, 1)};
                Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect));
            }
            case Snake.Direction.LEFT -> {
                Point2D[] expect = {new Point2D(-1, 0)};
                Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect));
            }
            case Snake.Direction.RIGHT -> {
                Point2D[] expect = {new Point2D(1, 0)};
                Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect));
            }
        }
        ;
    }

    @Test
    void gettersSettersTest() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);

        Assertions.assertEquals(snake.size(), 1);

        Assertions.assertEquals(snake.getTicksToMove(), 0);

        snake.setTicksToMove(1);

        Assertions.assertEquals(snake.getTicksToMove(), 1);
    }

    @Test
    void directionChangeTest() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        Assertions.assertEquals(snake.getLastMoveDirection(), Snake.Direction.RIGHT);
        snake.tick();

        Point2D[] expect1 = {new Point2D(1, 0)};
        Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect1));

        Assertions.assertEquals(snake.getLastMoveDirection(), Snake.Direction.RIGHT);
        snake.setPendingDirection(Snake.Direction.DOWN);
        Assertions.assertEquals(snake.getLastMoveDirection(), Snake.Direction.RIGHT);

        snake.tick();

        Point2D[] expect2 = {new Point2D(1, 1)};
        Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect2));
    }

    @Test
    void growTest() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        snake.grow();

        snake.tick();

        Point2D[] expect = {new Point2D(1, 0), new Point2D(0, 0)};
        Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect));
    }

    @Test
    void multipleGrowTest() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        snake.grow();
        snake.grow();

        snake.tick();
        snake.tick();

        Point2D[] expect = {new Point2D(2, 0), new Point2D(1, 0), new Point2D(0, 0)};
        Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect));
    }

    @Test
    void shrinkTest() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        snake.grow();
        snake.tick();

        snake.shrink();
        snake.tick();

        Point2D[] expect = {new Point2D(2, 0)};
        Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect));
    }

    @Test
    void multipleShrinkTest() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        snake.grow();
        snake.grow();
        snake.tick();

        snake.shrink();
        snake.shrink();
        snake.tick();

        Point2D[] expect = {new Point2D(2, 0)};
        Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect));
    }

    @Test
    void multipleMoveTest() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        snake.grow();
        snake.grow();
        snake.tick();
        snake.tick();

        Point2D[] expect = {new Point2D(2, 0), new Point2D(1, 0), new Point2D(0, 0)};
        Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect));

        snake.setPendingDirection(Snake.Direction.DOWN);

        snake.tick();

        Point2D[] expect2 = {new Point2D(2, 1), new Point2D(2, 0), new Point2D(1, 0)};
        Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect2));
    }

    @Test
    void multipleTicksMoveTest() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 1);
        snake.tick();

        Point2D[] expect = {new Point2D(0, 0)};
        Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect));

        snake.tick();

        Point2D[] expect2 = {new Point2D(1, 0)};
        Assertions.assertTrue(Arrays.equals(snake.getSegments().toArray(), expect2));
    }

    @Test
    void listenerTest() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        snake.addListener(
                c -> {
                    switch (c) {
                        case SnakeChangeListener.Change.Moved m -> {
                            Assertions.assertEquals(m.prevHead(), new Point2D(0, 0));
                            Assertions.assertEquals(m.newHead(), new Point2D(1, 0));
                            Assertions.assertEquals(m.newTail(), new Point2D(1, 0));
                            Assertions.assertEquals(m.prevTail(), new Point2D(0, 0));
                        }
                        default -> {}
                    }
                });

        snake.tick();
    }

    @Test
    void listenerTest2() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        snake.grow();
        snake.tick();

        snake.addListener(
                c -> {
                    switch (c) {
                        case SnakeChangeListener.Change.Moved m -> {
                            Assertions.assertEquals(m.prevHead(), new Point2D(1, 0));
                            Assertions.assertEquals(m.newHead(), new Point2D(2, 0));
                            Assertions.assertEquals(m.newTail(), new Point2D(1, 0));
                            Assertions.assertEquals(m.prevTail(), new Point2D(0, 0));
                        }
                        default -> {}
                    }
                });

        snake.tick();
    }

    @Test
    void listenerTest3() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);

        snake.addListener(
                c -> {
                    switch (c) {
                        case SnakeChangeListener.Change.Growed g -> {
                            Assertions.assertEquals(g.newTail(), new Point2D(0, 0));
                            Assertions.assertEquals(g.prevTail(), new Point2D(1, 0));
                        }
                        default -> {}
                    }
                });

        snake.grow();
        snake.tick();
    }

    @Test
    void listenerTest4() {
        ObservableSnake snake = new ObservableSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);

        snake.grow();
        snake.tick();

        snake.addListener(
                c -> {
                    switch (c) {
                        case SnakeChangeListener.Change.Shrinked s -> {
                            Assertions.assertEquals(s.newTail(), new Point2D(2, 0));
                            Assertions.assertEquals(s.prevTail(), new Point2D(1, 0));
                        }
                        default -> {}
                    }
                });

        snake.shrink();
        snake.tick();
    }
}
