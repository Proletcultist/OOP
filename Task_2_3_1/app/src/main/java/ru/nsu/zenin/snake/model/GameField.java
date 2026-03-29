package ru.nsu.zenin.snake.model;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import ru.nsu.zenin.collection.Point2D;

public class GameField {

    private final int width, height;

    TileState[] tiles;
    Map<Class<? extends TileState>, List<Point2D>> pointsByState;

    public GameField(int width, int height) {
        this(width, height, new HashMap<>());
    }

    public GameField(int width, int height, Map<Class<? extends TileState>, List<Point2D>> pointsByState) {
        this.width = width;
        this.height = height;
        tiles = new TileState[width * height];
        this.pointsByState = pointsByState;
    }

    public void setTileState(Point2D coord, TileState state) {
        tiles[coord.y() * width + coord.x()] = state;

        if (!pointsByState.containsKey(state.getClass())) {
            pointsByState.put(state.getClass(), new ArrayList<Point2D>());
        }

        pointsByState.get(state.getClass()).add(coord);
    }

    public sealed interface TileState {
        public record Free() implements TileState {}
        public record OccupiedBySnake(Snake snake) implements TileState {}
        public record OccupiedByApple(Apple apple) implements TileState {}
    }
}
