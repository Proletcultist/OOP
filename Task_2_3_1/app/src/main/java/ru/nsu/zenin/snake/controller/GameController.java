package ru.nsu.zenin.snake.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.control.ComboBox;
import javafx.util.Duration;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.Game;
import ru.nsu.zenin.snake.model.apple.AppleFactory;
import ru.nsu.zenin.snake.model.apple.BasicAppleFactory;
import ru.nsu.zenin.snake.model.apple.ShrinkingAppleFactory;
import ru.nsu.zenin.snake.model.Snake;
import ru.nsu.zenin.snake.model.TileState;
import ru.nsu.zenin.snake.model.apple.ShrinkingApple;
import ru.nsu.zenin.snake.view.GameFieldView;
import ru.nsu.zenin.snake.view.TileDrawer;
import ru.nsu.zenin.snake.view.FancyDrawer;
import ru.nsu.zenin.snake.view.DebugDrawer;
import ru.nsu.zenin.snake.model.apple.AppleFactory;
import ru.nsu.zenin.snake.model.bot.SnakeBot;
import java.util.List;
import java.util.function.Predicate;
import java.util.ArrayList;

public class GameController {
    @FXML private GameFieldView fieldView;
    @FXML private Text statusBar;
    @FXML private VBox modalContainer;
    @FXML private StackPane mainContainer;

    @FXML private ComboBox<LevelOption> difficultyInput;
    @FXML private TextField gridWidthInput;
    @FXML private TextField gridHeightInput;

    private int prevScore = 0;

    private Game game;

    @FXML
    private void initialize() {
        initGraphics();
        statusBar.setText("The Snake game");
    }

    private void initGraphics() {
        difficultyInput.getSelectionModel().selectFirst();
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
        statusBar.styleProperty().bind(
                Bindings.createStringBinding(() -> {
                    double fontSize = Math.rint((fieldView.getHeight() > fieldView.getWidth() ? fieldView.getWidth() : fieldView.getHeight()) * 0.04);
                    if (fontSize > 20) {
                        fontSize = 20;
                    }
                    return String.format("-fx-font-size: %.1fpx;", fontSize);
                },
                    fieldView.heightProperty(), fieldView.widthProperty()
                )
        );
        modalContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        modalContainer
                .paddingProperty()
                .bind(
                        Bindings.createObjectBinding(
                                () -> {
                                    double paddingH = modalContainer.getWidth() * 0.1;
                                    double paddingV = modalContainer.getHeight() * 0.1;
                                    return new Insets(paddingV, paddingH, paddingV, paddingH);
                                },
                                modalContainer.widthProperty(),
                                modalContainer.heightProperty()));
    }

    @FXML
    private void playPressed(ActionEvent event) {
        if (!gridWidthInput.getText().isEmpty()) {
            try {
                fieldView.setGridWidth(Integer.valueOf(gridWidthInput.getText()));
            }
            catch (NumberFormatException e) {
                statusBar.setText("Invalid int format for grid width");
            }
        }
        if (!gridHeightInput.getText().isEmpty()) {
            try {
                fieldView.setGridHeight(Integer.valueOf(gridHeightInput.getText()));
            }
            catch (NumberFormatException e) {
                statusBar.setText("Invalid int format for grid height");
            }
        }

        startNewGame(difficultyInput.getValue().getConfig());
        modalContainer.setVisible(false);
    }

    private void startNewGame(LevelConfig config) {
        fieldView.getField().setAll(new TileState.Free());

        game = new Game(fieldView.getField(), config.appleFactory(), 2);

        Snake playerSnake = game.createSnake(new Point2D(0, 0), Snake.Direction.RIGHT, 20);

        prevScore = game.getScore();
        statusBar.setText("Score: " + game.getScore());

        FancyDrawer drawer = new FancyDrawer(Color.BLACK, Color.GREEN, Color.GREEN);
        drawer.setAppleColor(ShrinkingApple.class, Color.BLUE);
        fieldView.setDrawer(drawer);

        Timeline timeline = new Timeline();

        timeline.getKeyFrames()
                .add(
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
                                        statusBar.setText(
                                                "Game Over! Your score: "
                                                        + game.getScore());
                                        modalContainer.setVisible(true);
                                    } else if (game.getState() == Game.State.WIN) {
                                        timeline.stop();
                                        statusBar.setText(
                                                "You won! Your score: "
                                                        + game.getScore());
                                        modalContainer.setVisible(true);
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
                        default -> {}
                    }
                });

        timeline.play();
        Platform.runLater(() -> fieldView.requestFocus());
    }

    private void tryChangeSnakeDirection(Snake snake, Snake.Direction dir) {
        if (snake.getLastMoveDirection().getOpposite() != dir) {
            snake.setPendingDirection(dir);
        }
    }

    private record LevelConfig(int ticksFrequency, AppleFactory appleFactory, List<SnakeBot> bots, Predicate<Game> winPredicate) {}

    @RequiredArgsConstructor
    public enum LevelOption {
        LEVEL1("Level 1", new LevelConfig(10, (new BasicAppleFactory()).combinedWith(new ShrinkingAppleFactory(), 0.2), new ArrayList<SnakeBot>(), g -> g.getScore() == 12)),
        LEVEL2("Level 2", new LevelConfig(10, (new BasicAppleFactory()).combinedWith(new ShrinkingAppleFactory(), 0.2), new ArrayList<SnakeBot>(), g -> g.getScore() == 12)),
        LEVEL3("Level 3", new LevelConfig(10, (new BasicAppleFactory()).combinedWith(new ShrinkingAppleFactory(), 0.2), new ArrayList<SnakeBot>(), g -> g.getScore() == 12));

        private final String asString;
        @Getter private final LevelConfig config;

        @Override
        public String toString() {
            return asString;
        }
    }

    @RequiredArgsConstructor
    public enum DrawerOption {
        FANCY("Fancy"),
        DEBUG("Debug"),
        DEFAULT("Default");

        private final String asString;

        @Override
        public String toString() {
            return asString;
        }
    }
}
