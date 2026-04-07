package ru.nsu.zenin.snake.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Map;
import java.util.HashMap;
import ru.nsu.zenin.snake.model.TileState;
import ru.nsu.zenin.snake.model.Snake;
import ru.nsu.zenin.snake.model.apple.Apple;
import ru.nsu.zenin.collection.Point2D;

public class FancyDrawer implements TileDrawer {
    private final static double HEAD_PADDING = 0.2;

    private final Color backgroundColor;
    private final Color defaultAppleColor;
    private final Color defaultSnakeColor;

    private final Map<Class<Apple>, Color> appleColors;
    private final Map<Snake, Color> snakeColors;

    public FancyDrawer(Color backgroundColor, Color defaultAppleColor, Color defaultSnakeColor) {
        this.appleColors = new HashMap<Class<Apple>, Color>();
        this.snakeColors = new HashMap<Snake, Color>();
        this.backgroundColor = backgroundColor;
        this.defaultSnakeColor = defaultSnakeColor;
        this.defaultAppleColor = defaultAppleColor;
    }

    public void draw(GraphicsContext ctx, Point2D coord, TileState tile, double x, double y, double width, double height) {
        switch (tile) {
            case TileState.Free free -> {
                ctx.setFill(backgroundColor);
                ctx.fillRect(x, y, width, height);
            }
            case TileState.OccupiedBySnake occS -> {
                Color color = snakeColors.get(occS.snake());
                if (color == null) {
                    color = defaultSnakeColor;
                }

                switch (occS) {
                    case TileState.OccupiedBySnake.SnakeHeadTail ht -> {
                        // Fill background
                        ctx.setFill(backgroundColor);
                        ctx.fillRect(x, y, width, height);

                        // Fill head
                        ctx.setFill(color);
                        ctx.fillRect(x + width * HEAD_PADDING / 2, y + height * HEAD_PADDING / 2, width - width * HEAD_PADDING, height - height * HEAD_PADDING);
                    }
                    case TileState.OccupiedBySnake.SnakeHead h -> {
                        // Fill background
                        ctx.setFill(backgroundColor);
                        ctx.fillRect(x, y, width, height);

                        ctx.setFill(color);

                        // Fill head
                        ctx.fillRect(x + width * HEAD_PADDING / 2, y + height * HEAD_PADDING / 2, width - width * HEAD_PADDING, height - height * HEAD_PADDING);
                        // Fill connection to body
                    }
                    case TileState.OccupiedBySnake.SnakeBody b -> {
                        ctx.setFill(color);
                        ctx.fillRect(x, y, width, height);
                    }
                    case TileState.OccupiedBySnake.SnakeTail t -> {
                        ctx.setFill(color);
                        ctx.fillRect(x, y, width, height);
                    }
                }
            }
            case TileState.OccupiedByApple occA -> {
                Color color = appleColors.get(occA.apple().getClass());
                if (color == null) {
                    color = defaultAppleColor;
                }
                ctx.setFill(color);
                ctx.fillRect(x, y, width, height);
            }
        }
    }
}
