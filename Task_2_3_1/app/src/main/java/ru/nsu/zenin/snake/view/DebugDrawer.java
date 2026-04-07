package ru.nsu.zenin.snake.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.nsu.zenin.snake.model.TileState;
import ru.nsu.zenin.collection.Point2D;

public class DebugDrawer implements TileDrawer {
    private final Color backgroundColor;

    public DebugDrawer(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void draw(GraphicsContext ctx, Point2D coord, TileState tile, double x, double y, double width, double height) {
        switch (tile) {
            case TileState.Free free -> {
                ctx.setFill(backgroundColor);
            }
            case TileState.OccupiedBySnake occS -> {
                switch (occS) {
                    case TileState.OccupiedBySnake.SnakeTail sT -> {
                        ctx.setFill(Color.BLUE);
                    }
                    case TileState.OccupiedBySnake.SnakeBody sB -> {
                        ctx.setFill(Color.GREEN);
                    }
                    case TileState.OccupiedBySnake.SnakeHead sH -> {
                        ctx.setFill(Color.RED);
                    }
                    case TileState.OccupiedBySnake.SnakeHeadTail sH -> {
                        ctx.setFill(Color.PURPLE);
                    }
                }
            }
            case TileState.OccupiedByApple occA -> {
                ctx.setFill(Color.ORANGE);
            }
        }
        ctx.fillRect(x, y, width, height);
    }
}
