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
import ru.nsu.zenin.collection.ObservableField;
import ru.nsu.zenin.collection.FieldChangeListener;
import ru.nsu.zenin.snake.model.TileState;

public class GameFieldView extends Region {

    private final ObservableField<TileState> field;
    private final IntegerProperty gridWidth;
    private final IntegerProperty gridHeight;

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

        field = new ObservableField<TileState>(new TileState.Free(), gridWidth.getValue(), gridHeight.getValue());

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
        
        field.addListener((FieldChangeListener<TileState>)
            change -> {
            switch ((TileState) change.state()) {
                case TileState.Free free -> redrawTile(change.point(), Color.BLACK);
                case TileState.OccupiedBySnake occS -> redrawTile(change.point(), Color.GREEN);
                case TileState.OccupiedByApple occA -> redrawTile(change.point(), Color.GREEN);
            }
        });
    }

    public void setGridWidth(final Integer value) {
        gridWidth.setValue(value);
    }

    public void setGridHeight(final Integer value) {
        gridHeight.setValue(value);
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
        Double pWidth = widthProperty().getValue();
        Double pHeight = heightProperty().getValue();

        Double factor = pWidth / gridWidth.getValue() > pHeight / gridHeight.getValue() ? pHeight / gridHeight.getValue() : pWidth / gridWidth.getValue();

        height = gridHeight.getValue() * factor;
        width = gridWidth.getValue() * factor;

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
                tileWidth,
                tileHeight);
    }

    private void redrawAll() {
        for (int i = 0; i < field.getWidth(); i++) {
            for (int j = 0; j < field.getHeight(); j++) {
                switch (field.get(i, j)) {
                    case TileState.Free free -> redrawTile(new Point2D(i, j), Color.BLACK);
                    case TileState.OccupiedBySnake occS -> redrawTile(new Point2D(i, j), Color.GREEN);
                    case TileState.OccupiedByApple occS -> redrawTile(new Point2D(i, j), Color.GREEN);
                }
            }
        }
    }
}
