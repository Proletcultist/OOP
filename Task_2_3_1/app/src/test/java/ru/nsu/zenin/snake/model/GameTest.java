package ru.nsu.zenin.snake.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.collection.Field;
import ru.nsu.zenin.collection.ObservableField;
import ru.nsu.zenin.snake.model.apple.AppleFactory;
import ru.nsu.zenin.snake.model.apple.Apple;
import ru.nsu.zenin.snake.model.apple.BasicApple;
import java.util.Arrays;
import java.util.Set;
import java.util.ArrayList;

public class GameTest {
    @Test
    void constructorTest() {
        Field<TileState> field = new ObservableField<TileState>(new TileState.Free(), 2, 2);
        AppleFactory factory = new MockAppleFactory();
        Game game = new Game(field, factory, 1, g -> false);

        Assertions.assertEquals(game.getState(), Game.State.RUNNING);
        Assertions.assertEquals(game.getScore(), 0);

        Assertions.assertEquals(game.getAvailable().size(), 3);
        Assertions.assertTrue(game.getAvailable().contains(new Point2D(0, 0)));
        Assertions.assertTrue(game.getAvailable().contains(new Point2D(1, 0)));
        Assertions.assertTrue(game.getAvailable().contains(new Point2D(0, 1)));

        Assertions.assertTrue(field.get(new Point2D(0, 0)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(1, 0)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(0, 1)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(1, 1)) instanceof TileState.OccupiedByApple);

        TileState.OccupiedByApple occA = (TileState.OccupiedByApple) field.get(new Point2D(1, 1));

        Assertions.assertTrue(occA.apple() instanceof BasicApple);
    }

    @ParameterizedTest
    @EnumSource(Snake.Direction.class)
    void tickTest(Snake.Direction dir) {
        Field<TileState> field = new ObservableField<TileState>(new TileState.Free(), 2, 2);
        AppleFactory factory = new MockAppleFactory();
        Game game = new Game(field, factory, 1, g -> false);

        Snake snake = game.createSnake(new Point2D(0, 0), dir, 0);
        game.tick();

        switch (dir) {
            case Snake.Direction.UP -> {
                Assertions.assertTrue(field.get(new Point2D(0, 0)) instanceof TileState.Free);
                Assertions.assertTrue(field.get(new Point2D(1, 0)) instanceof TileState.Free);
                Assertions.assertTrue(field.get(new Point2D(0, 1)) instanceof TileState.OccupiedBySnake.SnakeHeadTail);
                Assertions.assertTrue(field.get(new Point2D(1, 1)) instanceof TileState.OccupiedByApple);
                TileState.OccupiedBySnake occS = (TileState.OccupiedBySnake) field.get(new Point2D(0, 1));
                Assertions.assertEquals(occS.snake(), snake);
            }
            case Snake.Direction.DOWN -> {
                Assertions.assertTrue(field.get(new Point2D(0, 0)) instanceof TileState.Free);
                Assertions.assertTrue(field.get(new Point2D(1, 0)) instanceof TileState.Free);
                Assertions.assertTrue(field.get(new Point2D(0, 1)) instanceof TileState.OccupiedBySnake.SnakeHeadTail);
                Assertions.assertTrue(field.get(new Point2D(1, 1)) instanceof TileState.OccupiedByApple);
                TileState.OccupiedBySnake occS = (TileState.OccupiedBySnake) field.get(new Point2D(0, 1));
                Assertions.assertEquals(occS.snake(), snake);
            }
            case Snake.Direction.LEFT -> {
                Assertions.assertTrue(field.get(new Point2D(0, 0)) instanceof TileState.Free);
                Assertions.assertTrue(field.get(new Point2D(1, 0)) instanceof TileState.OccupiedBySnake.SnakeHeadTail);
                Assertions.assertTrue(field.get(new Point2D(0, 1)) instanceof TileState.Free);
                Assertions.assertTrue(field.get(new Point2D(1, 1)) instanceof TileState.OccupiedByApple);
                TileState.OccupiedBySnake occS = (TileState.OccupiedBySnake) field.get(new Point2D(1, 0));
                Assertions.assertEquals(occS.snake(), snake);
            }
            case Snake.Direction.RIGHT -> {
                Assertions.assertTrue(field.get(new Point2D(0, 0)) instanceof TileState.Free);
                Assertions.assertTrue(field.get(new Point2D(1, 0)) instanceof TileState.OccupiedBySnake.SnakeHeadTail);
                Assertions.assertTrue(field.get(new Point2D(0, 1)) instanceof TileState.Free);
                Assertions.assertTrue(field.get(new Point2D(1, 1)) instanceof TileState.OccupiedByApple);
                TileState.OccupiedBySnake occS = (TileState.OccupiedBySnake) field.get(new Point2D(1, 0));
                Assertions.assertEquals(occS.snake(), snake);
            }
        };

    }

    @Test
    void directionChangeTest() {
        Field<TileState> field = new ObservableField<TileState>(new TileState.Free(), 2, 2);
        AppleFactory factory = new MockAppleFactory();
        Game game = new Game(field, factory, 1, g -> false);

        Snake snake = game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        game.tick();

        Assertions.assertTrue(field.get(new Point2D(0, 0)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(1, 0)) instanceof TileState.OccupiedBySnake.SnakeHeadTail);
        Assertions.assertTrue(field.get(new Point2D(0, 1)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(1, 1)) instanceof TileState.OccupiedByApple);

        snake.setPendingDirection(Snake.Direction.DOWN);
        game.tick();

        Assertions.assertTrue(field.get(new Point2D(0, 0)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(1, 0)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(0, 1)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(1, 1)) instanceof TileState.OccupiedBySnake.SnakeHeadTail);
    }

    @Test
    void growTest() {
        Field<TileState> field = new ObservableField<TileState>(new TileState.Free(), 2, 2);
        AppleFactory factory = new MockAppleFactory();
        Game game = new Game(field, factory, 1, g -> false);

        Snake snake = game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        snake.grow();

        game.tick();

        Assertions.assertTrue(field.get(new Point2D(0, 0)) instanceof TileState.OccupiedBySnake.SnakeTail);
        Assertions.assertTrue(field.get(new Point2D(1, 0)) instanceof TileState.OccupiedBySnake.SnakeHead);
        Assertions.assertTrue(field.get(new Point2D(0, 1)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(1, 1)) instanceof TileState.OccupiedByApple);
    }

    @Test
    void growTest2() {
        Field<TileState> field = new ObservableField<TileState>(new TileState.Free(), 2, 2);
        AppleFactory factory = new MockAppleFactory();
        Game game = new Game(field, factory, 1, g -> false);

        Snake snake = game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        snake.grow();
        game.tick();

        snake.setPendingDirection(Snake.Direction.DOWN);
        snake.grow();
        game.tick();

        Assertions.assertTrue(field.get(new Point2D(0, 0)) instanceof TileState.OccupiedBySnake.SnakeTail);
        Assertions.assertTrue(field.get(new Point2D(1, 0)) instanceof TileState.OccupiedBySnake.SnakeBody);
        Assertions.assertTrue(field.get(new Point2D(0, 1)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(1, 1)) instanceof TileState.OccupiedBySnake.SnakeHead);
    }

    @Test
    void shrinkTest() {
        Field<TileState> field = new ObservableField<TileState>(new TileState.Free(), 2, 2);
        AppleFactory factory = new MockAppleFactory();
        Game game = new Game(field, factory, 1, g -> false);

        Snake snake = game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        snake.grow();
        game.tick();

        snake.setPendingDirection(Snake.Direction.DOWN);
        snake.shrink();
        game.tick();

        Assertions.assertTrue(field.get(new Point2D(0, 0)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(1, 0)) instanceof TileState.OccupiedBySnake.SnakeTail);
        Assertions.assertTrue(field.get(new Point2D(0, 1)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(1, 1)) instanceof TileState.OccupiedBySnake.SnakeHead);
    }

    @Test
    void appleGrow() {
        Field<TileState> field = new ObservableField<TileState>(new TileState.Free(), 2, 2);
        AppleFactory factory = new MockAppleFactory();
        Game game = new Game(field, factory, 1, g -> false);

        Snake snake = game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);

        game.setPlayerSnake(snake);

        game.tick();

        snake.setPendingDirection(Snake.Direction.DOWN);
        game.tick();
        game.tick();

        Assertions.assertEquals(game.getScore(), 1);

        Assertions.assertTrue(field.get(new Point2D(0, 0)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(1, 0)) instanceof TileState.OccupiedBySnake.SnakeHead);
        Assertions.assertTrue(field.get(new Point2D(0, 1)) instanceof TileState.Free);
        Assertions.assertTrue(field.get(new Point2D(1, 1)) instanceof TileState.OccupiedBySnake.SnakeTail);
    }

    @Test
    void winTest() {
        Field<TileState> field = new ObservableField<TileState>(new TileState.Free(), 2, 2);
        AppleFactory factory = new MockAppleFactory();
        Game game = new Game(field, factory, 1, g -> g.getScore() == 1);

        Snake snake = game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);

        game.setPlayerSnake(snake);

        game.tick();

        snake.setPendingDirection(Snake.Direction.DOWN);
        game.tick();
        game.tick();

        Assertions.assertEquals(game.getState(), Game.State.WIN);
    }

    @Test
    void gameOverTest() {
        Field<TileState> field = new ObservableField<TileState>(new TileState.Free(), 3, 3);
        AppleFactory factory = new MockAppleFactory();
        Game game = new Game(field, factory, 1, g -> g.getScore() == 1);

        Snake snake = game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        game.setPlayerSnake(snake);

        snake.grow();
        snake.grow();
        snake.grow();
        snake.grow();

        game.tick();
        game.tick();
        game.tick();
        game.tick();

        Assertions.assertEquals(game.getState(), Game.State.GAME_OVER);
    }

    @Test
    void gameOverTest2() {
        Field<TileState> field = new ObservableField<TileState>(new TileState.Free(), 5, 5);
        AppleFactory factory = new MockAppleFactory();
        Game game = new Game(field, factory, 1, g -> g.getScore() == 1);

        Snake snake = game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        game.setPlayerSnake(snake);

        snake.grow();
        snake.grow();
        snake.grow();
        snake.grow();

        game.tick();
        game.tick();
        game.tick();
        game.tick();

        snake.setPendingDirection(Snake.Direction.DOWN);
        game.tick();

        snake.setPendingDirection(Snake.Direction.LEFT);
        game.tick();

        snake.setPendingDirection(Snake.Direction.UP);
        game.tick();

        Assertions.assertEquals(game.getState(), Game.State.GAME_OVER);
    }

    @Test
    void killTest() {
        Field<TileState> field = new ObservableField<TileState>(new TileState.Free(), 5, 5);
        AppleFactory factory = new MockAppleFactory();
        Game game = new Game(field, factory, 1, g -> g.getScore() == 1);

        Snake snake = game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 0);
        Snake snake2 = game.createSnake(new Point2D(0, 2), Snake.Direction.RIGHT, 0);
        game.setPlayerSnake(snake);

        snake.grow();
        snake.grow();
        snake.grow();
        snake.grow();

        game.tick();
        game.tick();
        game.tick();
        game.tick();

        snake2.setPendingDirection(Snake.Direction.UP);

        game.tick();
        game.tick();
    
        field.forEach((p, s) -> {
            Assertions.assertFalse(s instanceof TileState.OccupiedBySnake && ((TileState.OccupiedBySnake) s).snake() == snake2);
        });

        Assertions.assertEquals(game.getState(), Game.State.RUNNING);
    }

    private class MockAppleFactory extends AppleFactory {
        public Apple create(Set<Point2D> set) {
            return new BasicApple(new Point2D(1, 1));
        }
    }
}
