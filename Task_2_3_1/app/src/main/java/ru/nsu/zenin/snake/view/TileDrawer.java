package ru.nsu.zenin.snake.view;

import javafx.scene.canvas.GraphicsContext;
import ru.nsu.zenin.snake.model.TileState;

@FunctionalInterface
public interface TileDrawer {
    void draw(GraphicsContext ctx, TileState tile, double x, double y, double width, double height);
}
