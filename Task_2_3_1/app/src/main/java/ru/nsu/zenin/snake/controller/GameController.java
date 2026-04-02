package ru.nsu.zenin.snake.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;
import javafx.application.Platform;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.Game;
import ru.nsu.zenin.snake.model.Snake;
import ru.nsu.zenin.snake.view.GameFieldView;
import ru.nsu.zenin.snake.model.apple.BasicAppleFactory;

public class GameController {

    @FXML private GameFieldView fieldView;

    Game game;

    public void initialize() {

        game = new Game(fieldView.getField(), new BasicAppleFactory(), 1);

        Snake playerSnake = game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 20);

        Timeline timeline =
                new Timeline(
                        new KeyFrame(
                                Duration.millis(10),
                                event -> {
                                    game.tick();
                                }));

        timeline.setCycleCount(Timeline.INDEFINITE);

        fieldView.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case KeyCode.UP -> playerSnake.changeDirection(Snake.Direction.UP);
                case KeyCode.DOWN -> playerSnake.changeDirection(Snake.Direction.DOWN);
                case KeyCode.LEFT -> playerSnake.changeDirection(Snake.Direction.LEFT);
                case KeyCode.RIGHT -> playerSnake.changeDirection(Snake.Direction.RIGHT);
                default -> {}
            }
        });

        Platform.runLater(() -> fieldView.requestFocus());
        timeline.play();
    }
}
