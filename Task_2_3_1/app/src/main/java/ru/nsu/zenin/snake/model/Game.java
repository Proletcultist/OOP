package ru.nsu.zenin.snake.model;

import java.util.ArrayList;
import java.util.LinkedList;
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
        ObservableSnake snake = new ObservableSnake(head, dir, ticksToMove);

        available.remove(head);
        field.set(head, new TileState.OccupiedBySnake.SnakeHeadTail(snake));

        snake.addListener(change -> {
            switch (change) {
                case SnakeChangeListener.Change.HeadMovedTo h -> {
                    Point2D transHead = pointsTranslator.apply(h.head());
                    Point2D transPrevHead = pointsTranslator.apply(h.prevHead());

                    if (field.contains(transHead)) {
                        // Check for collision
                        switch (field.get(transHead)) {
                            case TileState.OccupiedBySnake os -> state = State.GAME_OVER;
                            case TileState.OccupiedByApple oa -> {
                                oa.apple().apply(snake);
                                score++;
                                spawnNewApple();
                            }
                            case TileState.Free free -> {}
                        }

                        available.remove(transHead);

                        field.set(transHead, new TileState.OccupiedBySnake.SnakeHead(snake, transPrevHead));
                    }
                    if (field.contains(transPrevHead)) {
                        // If snake got tail for the first time
                        if (snake.size() == 2) {
                            field.set(transPrevHead, new TileState.OccupiedBySnake.SnakeTail(snake));
                        }
                        else {
                            TileState.OccupiedBySnake.SnakeHead prevHeadTile = (TileState.OccupiedBySnake.SnakeHead) field.get(transPrevHead);
                            field.set(transPrevHead, new TileState.OccupiedBySnake.SnakeBody(snake, prevHeadTile.next()));
                        }
                    }
                }
                case SnakeChangeListener.Change.TailMovedFrom t -> {
                    Point2D transTail = pointsTranslator.apply(t.tail());
                    Point2D transNewTail = pointsTranslator.apply(t.newTail());

                    if (field.contains(transTail)) {
                        available.add(transTail);
                        field.set(transTail, new TileState.Free());
                    }
                    if (field.contains(transNewTail)) {
                        // If only head left
                        if (snake.size() == 1) {
                            field.set(transNewTail, new TileState.OccupiedBySnake.SnakeHeadTail(snake));
                        }
                        else {
                            field.set(transNewTail, new TileState.OccupiedBySnake.SnakeTail(snake));
                        }
                    }
                }
            }
        });

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
