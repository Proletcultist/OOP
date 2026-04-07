package ru.nsu.zenin.snake.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.Game;
import ru.nsu.zenin.snake.model.Snake;
import ru.nsu.zenin.snake.model.TileState;
import ru.nsu.zenin.snake.model.apple.BasicAppleFactory;
import ru.nsu.zenin.snake.view.GameFieldView;
import ru.nsu.zenin.snake.view.FancyDrawer;
import ru.nsu.zenin.snake.view.DebugDrawer;

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
        fieldView.setDrawer(new FancyDrawer(Color.BLACK, Color.GREEN, Color.GREEN));
        // fieldView.setDrawer(new DebugDrawer(Color.BLACK));

        fieldView
                .paddingProperty()
                .bind(
                        Bindings.createObjectBinding(
                                () -> {
                                    double paddingH = fieldView.getWidth() * 0.1;
                                    double paddingV = fieldView.getHeight() * 0.1;
                                    return new Insets(paddingV, paddingH, paddingV, paddingH);
                                },
                                fieldView.widthProperty(),
                                fieldView.heightProperty()));
    }

    private void startNewGame() {
        fieldView.getField().setAll(new TileState.Free());

        game = new Game(fieldView.getField(), new BasicAppleFactory(), 1);

        prevScore = game.getScore();
        statusBar.setText("Score: " + game.getScore());

        Snake playerSnake = game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 20);

        Timeline timeline = new Timeline();

        timeline.getKeyFrames()
                .add(
                        new KeyFrame(
                                Duration.millis(25),
                                event -> {
                                    game.tick();

                                    if (game.getScore() != prevScore) {
                                        statusBar.setText("Score: " + game.getScore());
                                        prevScore = game.getScore();
                                    }

                                    if (game.getState() == Game.State.GAME_OVER) {
                                        timeline.stop();
                                        statusBar.setText(
                                                "Game Over! Your score: "
                                                        + game.getScore()
                                                        + " (Press R for restart)");
                                    } else if (game.getState() == Game.State.WIN) {
                                        timeline.stop();
                                        statusBar.setText(
                                                "You won! Your score: "
                                                        + game.getScore()
                                                        + " (Press R for restart)");
                                    }
                                }));

        timeline.setCycleCount(Timeline.INDEFINITE);

        fieldView.setOnKeyPressed(
                e -> {
                    switch (e.getCode()) {
                        case KeyCode.UP -> tryChangeSnakeDirection(playerSnake, Snake.Direction.UP);
                        case KeyCode.DOWN ->
                                tryChangeSnakeDirection(playerSnake, Snake.Direction.DOWN);
                        case KeyCode.LEFT ->
                                tryChangeSnakeDirection(playerSnake, Snake.Direction.LEFT);
                        case KeyCode.RIGHT ->
                                tryChangeSnakeDirection(playerSnake, Snake.Direction.RIGHT);
                        case KeyCode.R -> {
                            timeline.stop();
                            startNewGame();
                        }
                        default -> {}
                    }
                });

        timeline.play();
    }

    private void tryChangeSnakeDirection(Snake snake, Snake.Direction dir) {
        if (snake.getLastMoveDirection().getOpposite() != dir) {
            snake.setPendingDirection(dir);
        }
    }
}
