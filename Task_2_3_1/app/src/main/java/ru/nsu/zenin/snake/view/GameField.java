package ru.nsu.zenin.snake.view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import ru.nsu.zenin.collection.Point2D;

public class GameField extends Region {

    private final ObservableList<ObservableSnake> snakes = FXCollections.observableArrayList();
    private final ObservableList<Point2D> apples = FXCollections.observableArrayList();
    private final ObjectProperty<Color> backgroundColor;
    private final IntegerProperty gridWidth;
    private final IntegerProperty gridHeight;

    private Canvas canvas;
    private GraphicsContext ctx;
    private Double width;
    private Double height;

    public GameField() {
        gridWidth =
                new IntegerPropertyBase(1) {
                    @Override
                    protected void invalidated() {
                        redrawAll();
                    }

                    @Override
                    public Object getBean() {
                        return GameField.this;
                    }

                    @Override
                    public String getName() {
                        return "gridWidth";
                    }
                };
        gridHeight =
                new IntegerPropertyBase(1) {
                    @Override
                    protected void invalidated() {
                        redrawAll();
                    }

                    @Override
                    public Object getBean() {
                        return GameField.this;
                    }

                    @Override
                    public String getName() {
                        return "gridHeight";
                    }
                };
        backgroundColor =
                new ObjectPropertyBase<Color>(Color.BLACK) {
                    @Override
                    protected void invalidated() {
                        redrawAll();
                    }

                    @Override
                    public Object getBean() {
                        return GameField.this;
                    }

                    @Override
                    public String getName() {
                        return "backgroundColor";
                    }
                };

        initGraphics();
        initListeners();
    }

    private void initGraphics() {
        canvas = new Canvas(getPrefWidth(), getPrefHeight());
        ctx = canvas.getGraphicsContext2D();

        width = getPrefWidth();
        height = getPrefHeight();

        getChildren().setAll(canvas);
    }

    private void initListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());

        backgroundColor.addListener(p -> redrawAll());

        snakes.addListener(
                (ListChangeListener<ObservableSnake>)
                        change -> {
                            while (change.next()) {
                                if (change.wasAdded()) {
                                    for (ObservableSnake snake : change.getAddedSubList()) {
                                        for (Point2D segment : snake.getSegments()) {
                                            redrawTile(segment, snake.getColor());
                                        }
                                        snake.addListener(
                                                (SnakeChangeListener)
                                                        snakeChange -> {
                                                            switch (snakeChange.act()) {
                                                                case OCCUPIED ->
                                                                        redrawTile(
                                                                                snakeChange.point(),
                                                                                snakeChange
                                                                                        .snake()
                                                                                        .getColor());
                                                                case LEFT ->
                                                                        redrawTile(
                                                                                snakeChange.point(),
                                                                                this.backgroundColor
                                                                                        .getValue());
                                                            }
                                                        });
                                    }
                                } else if (change.wasRemoved()) {
                                    for (ObservableSnake snake : change.getRemoved()) {
                                        for (Point2D segment : snake.getSegments()) {
                                            redrawTile(segment, this.backgroundColor.getValue());
                                        }
                                    }
                                }
                            }
                        });
        apples.addListener(
                (ListChangeListener<Point2D>)
                        change -> {
                            while (change.next()) {
                                if (change.wasAdded()) {
                                    for (Point2D apple : change.getAddedSubList()) {
                                        redrawTile(apple, Color.RED);
                                    }
                                } else if (change.wasRemoved()) {
                                    for (Point2D apple : change.getRemoved()) {
                                        redrawTile(apple, this.backgroundColor.getValue());
                                    }
                                }
                            }
                        });
    }

    public void setBackgroundColor(final Color color) {
        this.backgroundColor.setValue(color);
    }

    public void setGridWidth(final Integer value) {
        gridWidth.setValue(value);
    }

    public void setGridHeight(final Integer value) {
        gridHeight.setValue(value);
    }

    public ObservableList<ObservableSnake> getSnakes() {
        return this.snakes;
    }

    public ObservableList<Point2D> getApples() {
        return this.apples;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor.getValue();
    }

    public Integer getGridWidth() {
        return this.gridWidth.getValue();
    }

    public Integer getGridHeight() {
        return this.gridHeight.getValue();
    }

    private void resize() {
        Double pWidth = widthProperty().getValue();
        Double pHeight = heightProperty().getValue();

        if (gridHeight.getValue() > gridWidth.getValue()) {
            height = pHeight;
            width = height * gridWidth.getValue() / gridHeight.getValue();
        } else {
            width = pWidth;
            height = width * gridHeight.getValue() / gridWidth.getValue();
        }

        canvas.setWidth(width);
        canvas.setHeight(height);

        canvas.relocate((pWidth - width) * 0.5, (pHeight - height) * 0.5);

        redrawAll();
    }

    private void redrawTile(Point2D coord, Color color) {
        Double tileWidth = width / gridWidth.getValue();
        Double tileHeight = height / gridHeight.getValue();
        ctx.setFill(color);
        ctx.fillRect(
                coord.x() * tileWidth,
                coord.y() * tileHeight,
                (coord.x() + 1) * tileWidth,
                (coord.y() + 1) * tileHeight);
    }

    private void redrawAll() {
        ctx.setFill(backgroundColor.getValue());
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Point2D apple : apples) {
            redrawTile(apple, Color.GREEN);
        }

        for (ObservableSnake snake : snakes) {
            for (Point2D segment : snake.getSegments()) {
                redrawTile(segment, snake.getColor());
            }
        }
    }
}
