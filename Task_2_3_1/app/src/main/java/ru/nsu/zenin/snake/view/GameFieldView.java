package ru.nsu.zenin.snake.view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import ru.nsu.zenin.collection.FieldChangeListener;
import ru.nsu.zenin.collection.ObservableField;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.TileState;

public class GameFieldView extends Region {
    private static double TILE_BOARDER = 0.4;

    private final ObservableField<TileState> field;
    private final IntegerProperty gridWidth;
    private final IntegerProperty gridHeight;
    private TileDrawer drawer;

    private Canvas canvas;
    private GraphicsContext ctx;
    private Double width;
    private Double height;

    public GameFieldView() {
        gridWidth =
                new IntegerPropertyBase(1) {
                    @Override
                    protected void invalidated() {
                        resizeGrid();
                    }

                    @Override
                    public Object getBean() {
                        return GameFieldView.this;
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
                        resizeGrid();
                    }

                    @Override
                    public Object getBean() {
                        return GameFieldView.this;
                    }

                    @Override
                    public String getName() {
                        return "gridHeight";
                    }
                };

        field =
                new ObservableField<TileState>(
                        new TileState.Free(), gridWidth.getValue(), gridHeight.getValue());

        initGraphics();
        initListeners();
    }

    private void initGraphics() {
        drawer =
                (ctx, coord, tile, x, y, width, height) -> {
                    switch (tile) {
                        case TileState.Free free -> {
                            ctx.setFill(Color.BLACK);
                        }
                        case TileState.SnakeTail sT -> {
                            ctx.setFill(Color.GREEN);
                        }
                        case TileState.SnakeBody sB -> {
                            ctx.setFill(Color.GREEN);
                        }
                        case TileState.SnakeHead sH -> {
                            ctx.setFill(Color.GREEN);
                        }
                        case TileState.AppleTile occA -> {
                            ctx.setFill(Color.GREEN);
                        }
                    }
                    ctx.fillRect(x, y, width, height);
                };

        canvas = new Canvas(getPrefWidth(), getPrefHeight());
        ctx = canvas.getGraphicsContext2D();

        width = getPrefWidth();
        height = getPrefHeight();

        getChildren().setAll(canvas);
    }

    private void initListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        paddingProperty().addListener(o -> resize());

        field.addListener(
                (FieldChangeListener<TileState>)
                        change -> {
                            redrawTile(change.point(), change.state());
                        });
    }

    public void setGridWidth(final Integer value) {
        gridWidth.setValue(value);
    }

    public void setGridHeight(final Integer value) {
        gridHeight.setValue(value);
    }

    public void setDrawer(TileDrawer drawer) {
        this.drawer = drawer;
        redrawAll();
    }

    public Integer getGridWidth() {
        return this.gridWidth.getValue();
    }

    public Integer getGridHeight() {
        return this.gridHeight.getValue();
    }

    public ObservableField<TileState> getField() {
        return field;
    }

    private void resizeGrid() {
        field.resize(new TileState.Free(), gridWidth.getValue(), gridHeight.getValue());
        redrawAll();
    }

    private void resize() {
        Double pWidth = getWidth() - getInsets().getLeft() - getInsets().getRight();
        Double pHeight = getHeight() - getInsets().getTop() - getInsets().getBottom();

        Double factor =
                pWidth / gridWidth.getValue() > pHeight / gridHeight.getValue()
                        ? pHeight / gridHeight.getValue()
                        : pWidth / gridWidth.getValue();

        height = Math.rint(gridHeight.getValue() * factor);
        width = Math.rint(gridWidth.getValue() * factor);

        canvas.setWidth(width);
        canvas.setHeight(height);

        canvas.relocate(
                Math.rint((getWidth() - width) * 0.5), Math.rint((getHeight() - height) * 0.5));

        redrawAll();
    }

    private void redrawTile(Point2D coord, TileState state) {
        Double tileWidth = width / gridWidth.getValue();
        Double tileHeight = height / gridHeight.getValue();

        drawer.draw(
                ctx,
                coord,
                state,
                Math.rint(coord.x() * tileWidth - TILE_BOARDER / 2),
                Math.rint(coord.y() * tileHeight - TILE_BOARDER / 2),
                Math.rint(tileWidth + TILE_BOARDER),
                Math.rint(tileHeight + TILE_BOARDER));
    }

    private void redrawAll() {
        field.forEach(
                (point, state) -> {
                    redrawTile(point, state);
                });
    }
}
