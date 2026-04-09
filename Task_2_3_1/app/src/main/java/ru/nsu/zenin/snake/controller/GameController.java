package ru.nsu.zenin.snake.controller;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import ru.nsu.zenin.snake.model.Game;
import ru.nsu.zenin.snake.model.Snake;
import ru.nsu.zenin.snake.model.TileState;
import ru.nsu.zenin.snake.model.apple.BasicAppleFactory;
import ru.nsu.zenin.snake.model.apple.ShrinkingApple;
import ru.nsu.zenin.snake.model.apple.ShrinkingAppleFactory;
import ru.nsu.zenin.snake.model.bot.ClosestAppleSnakeBot;
import ru.nsu.zenin.snake.model.bot.RandomSnakeBot;
import ru.nsu.zenin.snake.model.bot.SnakeBot;
import ru.nsu.zenin.snake.view.DebugDrawer;
import ru.nsu.zenin.snake.view.FancyDrawer;
import ru.nsu.zenin.snake.view.GameFieldView;
import ru.nsu.zenin.util.Random;

public class GameController {
    @FXML private GameFieldView fieldView;
    @FXML private Text statusBar;
    @FXML private VBox modalContainer;
    @FXML private StackPane mainContainer;

    @FXML private ComboBox<LevelOption> difficultyInput;
    @FXML private ComboBox<DrawerOption> drawerInput;
    @FXML private TextField gridWidthInput;
    @FXML private TextField gridHeightInput;

    private final List<SnakeBot> bots = new ArrayList<SnakeBot>();

    private final ComboBox<Color> playerColors =
            new ComboBox<Color>(
                    FXCollections.observableArrayList(Color.GREEN, Color.PURPLE, Color.RED));
    private final ComboBox<Color> botsColors =
            new ComboBox<Color>(
                    FXCollections.observableArrayList(Color.GREEN, Color.PURPLE, Color.RED));

    private int prevScore = 0;

    private Game game;

    @FXML
    private void initialize() {
        initGraphics();
        statusBar.setText("The Snake game");
    }

    private void initGraphics() {
        playerColors.setEditable(false);
        botsColors.setEditable(false);
        difficultyInput.getSelectionModel().selectFirst();
        drawerInput.getSelectionModel().selectFirst();
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
        statusBar
                .styleProperty()
                .bind(
                        Bindings.createStringBinding(
                                () -> {
                                    double fontSize =
                                            Math.rint(
                                                    (fieldView.getHeight() > fieldView.getWidth()
                                                                    ? fieldView.getWidth()
                                                                    : fieldView.getHeight())
                                                            * 0.04);
                                    if (fontSize > 20) {
                                        fontSize = 20;
                                    }
                                    return String.format("-fx-font-size: %.1fpx;", fontSize);
                                },
                                fieldView.heightProperty(),
                                fieldView.widthProperty()));
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
            } catch (NumberFormatException e) {
                statusBar.setText("Invalid int format for grid width");
            }
        }
        if (!gridHeightInput.getText().isEmpty()) {
            try {
                fieldView.setGridHeight(Integer.valueOf(gridHeightInput.getText()));
            } catch (NumberFormatException e) {
                statusBar.setText("Invalid int format for grid height");
            }
        }

        startNewGame();
        modalContainer.setVisible(false);
    }

    private void startNewGame() {
        configureGame();

        prevScore = game.getScore();
        statusBar.setText("Score: " + game.getScore());

        Timeline timeline = new Timeline();
        timeline.getKeyFrames()
                .add(
                        new KeyFrame(
                                Duration.millis(10),
                                event -> {
                                    for (SnakeBot bot : bots) {
                                        bot.tick();
                                    }
                                    game.tick();

                                    if (game.getScore() != prevScore) {
                                        statusBar.setText("Score: " + game.getScore());
                                        prevScore = game.getScore();
                                    }

                                    if (game.getState() == Game.State.GAME_OVER) {
                                        timeline.stop();
                                        statusBar.setText(
                                                "Game Over! Your score: " + game.getScore());
                                        modalContainer.setVisible(true);
                                    } else if (game.getState() == Game.State.WIN) {
                                        timeline.stop();
                                        statusBar.setText(
                                                "You won! Your score: " + game.getScore());
                                        modalContainer.setVisible(true);
                                    }
                                }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void tryChangeSnakeDirection(Snake snake, Snake.Direction dir) {
        if (snake.getLastMoveDirection().getOpposite() != dir) {
            snake.setPendingDirection(dir);
        }
    }

    private void configureGame() {
        bots.clear();
        fieldView.getField().setAll(new TileState.Free());

        game =
                switch (difficultyInput.getValue()) {
                    case LevelOption.LEVEL1 ->
                            new Game(
                                    fieldView.getField(),
                                    (new BasicAppleFactory())
                                            .combinedWith(new ShrinkingAppleFactory(), 0.2),
                                    5,
                                    g -> g.getScore() == 12);
                    case LevelOption.LEVEL2 ->
                            new Game(
                                    fieldView.getField(),
                                    (new BasicAppleFactory())
                                            .combinedWith(new ShrinkingAppleFactory(), 0.2),
                                    2,
                                    g -> g.getScore() == 20);
                    case LevelOption.LEVEL3 ->
                            new Game(
                                    fieldView.getField(),
                                    (new BasicAppleFactory())
                                            .combinedWith(new ShrinkingAppleFactory(), 0.2),
                                    1,
                                    g -> g.getScore() == 25);
                };
        Snake playerSnake =
                switch (difficultyInput.getValue()) {
                    case LevelOption.LEVEL1 ->
                            game.createSnake(
                                    Random.getRandomFromSet(game.getAvailable()),
                                    Snake.Direction.RIGHT,
                                    15);
                    case LevelOption.LEVEL2 ->
                            game.createSnake(
                                    Random.getRandomFromSet(game.getAvailable()),
                                    Snake.Direction.RIGHT,
                                    10);
                    case LevelOption.LEVEL3 ->
                            game.createSnake(
                                    Random.getRandomFromSet(game.getAvailable()),
                                    Snake.Direction.RIGHT,
                                    5);
                };

        game.setPlayerSnake(playerSnake);

        switch (difficultyInput.getValue()) {
            case LevelOption.LEVEL1 -> {}
            case LevelOption.LEVEL2 -> {
                Snake botSnake =
                        game.createSnake(
                                Random.getRandomFromSet(game.getAvailable()),
                                Snake.Direction.RIGHT,
                                10);
                bots.add(new RandomSnakeBot(botSnake, fieldView.getField()));
            }
            case LevelOption.LEVEL3 -> {
                Snake botSnake1 =
                        game.createSnake(
                                Random.getRandomFromSet(game.getAvailable()),
                                Snake.Direction.RIGHT,
                                10);
                Snake botSnake2 =
                        game.createSnake(
                                Random.getRandomFromSet(game.getAvailable()),
                                Snake.Direction.RIGHT,
                                10);
                bots.add(new RandomSnakeBot(botSnake1, fieldView.getField()));
                bots.add(new ClosestAppleSnakeBot(botSnake2, fieldView.getField()));
            }
        }

        switch (drawerInput.getValue()) {
            case DrawerOption.FANCY -> {
                FancyDrawer drawer = new FancyDrawer(Color.BLACK, Color.GREEN, Color.GREEN);
                for (SnakeBot s : bots) {
                    drawer.setSnakeColor(s.getSnake(), Color.RED);
                }
                drawer.setAppleColor(ShrinkingApple.class, Color.BLUE);
                fieldView.setDrawer(drawer);
            }
            case DrawerOption.DEBUG -> {
                DebugDrawer drawer = new DebugDrawer(Color.BLACK);
                fieldView.setDrawer(drawer);
            }
        }

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
                        case KeyCode.Q ->
                                game.stop();
                        default -> {}
                    }
                });
        Platform.runLater(() -> fieldView.requestFocus());
    }

    @RequiredArgsConstructor
    public enum LevelOption {
        LEVEL1("Level 1"),
        LEVEL2("Level 2"),
        LEVEL3("Level 3");

        private final String asString;

        @Override
        public String toString() {
            return asString;
        }
    }

    @RequiredArgsConstructor
    public enum DrawerOption {
        FANCY("Fancy"),
        DEBUG("Debug");

        private final String asString;

        @Override
        public String toString() {
            return asString;
        }
    }
}
