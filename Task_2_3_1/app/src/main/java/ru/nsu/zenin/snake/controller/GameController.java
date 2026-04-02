package ru.nsu.zenin.snake.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.util.Duration;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.Game;
import ru.nsu.zenin.snake.model.Snake;
import ru.nsu.zenin.snake.view.GameFieldView;

public class GameController {

    @FXML private GameFieldView fieldView;

    Game game;

    public void initialize() {
        game = new Game(fieldView.getField());

        game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 20);

        Timeline timeline =
                new Timeline(
                        new KeyFrame(
                                Duration.millis(50),
                                event -> {
                                    game.tick();
                                }));

        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.play();
    }
}
