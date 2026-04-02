package ru.nsu.zenin.snake.model;

import ru.nsu.zenin.snake.model.apple.Apple;

public sealed interface TileState {
    public record Free() implements TileState {}

    public record OccupiedBySnake(Snake snake) implements TileState {}

    public record OccupiedByApple(Apple apple) implements TileState {}
}
