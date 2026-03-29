package ru.nsu.zenin.snake.controller;

import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.view.ObservableSnake;

public class GameController {

    @FXML
    private ObservableList<ObservableSnake> snakes;
    @FXML
    private ObservableList<Point2D> apples;
    @FXML
    private IntegerProperty gridWidth;
    @FXML
    private IntegerProperty gridHeight;

    // Game game;

    public void initialize() {
        // game = new Game(gridWidth.getValue(), gridHeight.getValue(), );
    }
}
