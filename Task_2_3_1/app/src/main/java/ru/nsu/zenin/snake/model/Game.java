package ru.nsu.zenin.snake.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import ru.nsu.zenin.collection.Field;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.apple.Apple;
import ru.nsu.zenin.snake.model.apple.AppleFactory;

public class Game {
    private final Field<TileState> field;
    private final Set<Point2D> available;
    private final AppleFactory appleFactory;
    private final List<Snake> snakes = new ArrayList<Snake>();
    private final Function<Point2D, Point2D> pointsTranslator;
    private final Predicate<Game> winPredicate;
    private State state;

    private int score = 0;

    public Game(Field<TileState> field, AppleFactory appleFactory, int applesAmount) {
        this(
                field,
                appleFactory,
                applesAmount,
                p -> p.wrappedAround(field.getWidth(), field.getHeight()),
                g -> g.getScore() == 12);
    }

    public Game(
            Field<TileState> field,
            AppleFactory appleFactory,
            int applesAmount,
            Function<Point2D, Point2D> pointsTranslator,
            Predicate<Game> winPredicate) {
        this.field = field;
        this.appleFactory = appleFactory;
        this.available = new HashSet<Point2D>();
        this.state = State.RUNNING;
        this.pointsTranslator = pointsTranslator;
        this.winPredicate = winPredicate;

        field.forEach(
                (point, state) -> {
                    if (state instanceof TileState.Free) {
                        available.add(point);
                    }
                });

        for (int i = 0; i < applesAmount; i++) {
            spawnNewApple();
        }
    }

    public Snake createSnake(Point2D head, Snake.Direction dir, Integer ticksToMove) {
        ObservableList<Point2D> segments = FXCollections.observableArrayList();
        segments.add(head);
        Snake snake = new Snake(segments, dir, ticksToMove);

        available.remove(head);

        segments.addListener(
                (ListChangeListener<Point2D>)
                        change -> {
                            while (change.next()) {
                                if (change.wasAdded()) {
                                    for (Point2D segment : change.getAddedSubList()) {
                                        segment = pointsTranslator.apply(segment);

                                        // Check for collision
                                        if (!available.contains(segment)) {
                                            switch (field.get(segment)) {
                                                case TileState.Free free -> {}
                                                case TileState.OccupiedBySnake os ->
                                                        state = State.GAME_OVER;
                                                case TileState.OccupiedByApple oa -> {
                                                    oa.apple().apply(snake);
                                                    score++;
                                                    field.set(
                                                            segment,
                                                            new TileState.OccupiedBySnake(snake));
                                                    spawnNewApple();
                                                }
                                            }
                                        } else {
                                            available.remove(segment);
                                            field.set(
                                                    segment, new TileState.OccupiedBySnake(snake));
                                        }
                                    }
                                } else if (change.wasRemoved()) {
                                    for (Point2D segment : change.getRemoved()) {
                                        segment = pointsTranslator.apply(segment);
                                        if (field.contains(segment)) {
                                            available.add(segment);
                                            field.set(segment, new TileState.Free());
                                        }
                                    }
                                }
                            }
                        });

        field.set(head, new TileState.OccupiedBySnake(snake));

        snakes.add(snake);

        return snake;
    }

    public State getState() {
        return state;
    }

    public int getScore() {
        return score;
    }

    private void spawnNewApple() {
        Apple apple = appleFactory.create(available);
        available.remove(apple.getPosition());
        field.set(apple.getPosition(), new TileState.OccupiedByApple(apple));
    }

    public void tick() {
        if (state == State.RUNNING) {
            for (Snake snake : snakes) {
                snake.tick();
            }
        }

        if (winPredicate.test(this)) {
            state = State.WIN;
        }
    }

    public enum State {
        RUNNING,
        GAME_OVER,
        WIN
    }
}
