package ru.nsu.zenin.snake.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.nsu.zenin.snake.model.TileState;
import ru.nsu.zenin.collection.Point2D;

public class FancyDrawer implements TileDrawer {
    private final Color backgroundColor;

    public FancyDrawer(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void draw(GraphicsContext ctx, Point2D coord, TileState tile, double x, double y, double width, double height) {
        switch (tile) {
            case TileState.Free free -> {
                ctx.setFill(backgroundColor);
                ctx.fillRect(x, y, width, height);
            }
            case TileState.OccupiedBySnake sT -> {
                ctx.setFill(Color.GREEN);
                ctx.fillRect(x, y, width, height);
            }
            case TileState.OccupiedByApple occA -> {
                ctx.setFill(Color.GREEN);
                ctx.fillRect(x, y, width, height);
            }
        }
    }
}
