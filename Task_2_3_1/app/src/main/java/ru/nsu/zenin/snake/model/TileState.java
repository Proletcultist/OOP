package ru.nsu.zenin.snake.model;

import ru.nsu.zenin.snake.model.apple.Apple;
import ru.nsu.zenin.collection.Point2D;

public sealed interface TileState {
    public record Free() implements TileState {}

    public record SnakeHead(Snake snake, Point2D next) implements TileState {}
    public record SnakeBody(Snake snake, Point2D next) implements TileState {}
    public record SnakeTail(Snake snake) implements TileState {}

    public record AppleTile(Apple apple) implements TileState {}
}
