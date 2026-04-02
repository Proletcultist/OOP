package ru.nsu.zenin.snake.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.Game;
import ru.nsu.zenin.snake.model.Snake;
import ru.nsu.zenin.snake.view.GameFieldView;
import ru.nsu.zenin.snake.model.apple.BasicAppleFactory;

public class GameController {

    @FXML private GameFieldView fieldView;
    @FXML private Text statusBar;

    private int prevScore = 0;

    Game game;

    public void initialize() {
        initGraphics();
        startNewGame();
        Platform.runLater(() -> fieldView.requestFocus());
    }

    private void initGraphics() {
         fieldView.paddingProperty().bind(
                 Bindings.createObjectBinding(() -> {
                     double paddingH = fieldView.getWidth() * 0.1;
                     double paddingV = fieldView.getHeight() * 0.1;
                     return new Insets(paddingV, paddingH, paddingV, paddingH);
                 }, fieldView.widthProperty(), fieldView.heightProperty())
         );
    }

    private void startNewGame() {

        game = new Game(fieldView.getField(), new BasicAppleFactory(), 1);

        prevScore = game.getScore();
        statusBar.setText("Score: " + game.getScore());

        Snake playerSnake = game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 20);

        Timeline timeline = new Timeline();

        timeline.getKeyFrames().add(
                        new KeyFrame(
                                Duration.millis(10),
                                event -> {
                                    game.tick();

                                    if (game.getScore() != prevScore) {
                                        statusBar.setText("Score: " + game.getScore());
                                        prevScore = game.getScore();
                                    }

                                    if (game.getState() == Game.State.GAME_OVER) {
                                        timeline.stop();
                                        statusBar.setText("Game Over! Your score: " + game.getScore() + " (Press R for restart)");
                                    }
                                    else if (game.getState() == Game.State.WIN) {
                                        timeline.stop();
                                        statusBar.setText("You won! Your score: " + game.getScore() + " (Press R for restart)");
                                    }
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

        timeline.play();
    }
}
