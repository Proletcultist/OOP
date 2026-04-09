package ru.nsu.zenin.snake.model;

import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.apple.Apple;

public sealed interface TileState {
    public record Free() implements TileState {}

    public sealed interface OccupiedBySnake extends TileState {
        Snake snake();

        public record SnakeHeadTail(Snake snake, boolean dead) implements OccupiedBySnake {}

        public record SnakeHead(Snake snake, Point2D next, boolean dead)
                implements OccupiedBySnake {}

        public record SnakeBody(Snake snake, Point2D next, Point2D prev)
                implements OccupiedBySnake {}

        public record SnakeTail(Snake snake, Point2D prev) implements OccupiedBySnake {}
    }

    public record OccupiedByApple(Apple apple) implements TileState {}
}
