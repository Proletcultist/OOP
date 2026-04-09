package ru.nsu.zenin.snake.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import ru.nsu.zenin.collection.Field;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.apple.Apple;
import ru.nsu.zenin.snake.model.apple.AppleFactory;

public class Game {
    private final Field<TileState> field;
    private final Set<Point2D> available;
    private final AppleFactory appleFactory;
    private final List<Snake> snakes = new ArrayList<Snake>();
    private final List<Snake> pendingToRemove = new ArrayList<Snake>();
    private final Predicate<Game> winPredicate;
    private State state;

    private Snake playerSnake = null;

    private int score = 0;

    public Game(Field<TileState> field, AppleFactory appleFactory, int applesAmount) {
        this(field, appleFactory, applesAmount, g -> g.getScore() == 12);
    }

    public Game(
            Field<TileState> field,
            AppleFactory appleFactory,
            int applesAmount,
            Predicate<Game> winPredicate) {
        this.field = field;
        this.appleFactory = appleFactory;
        this.available = new HashSet<Point2D>();
        this.state = State.RUNNING;
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
        field.set(head, new TileState.OccupiedBySnake.SnakeHeadTail(snake, false));

        snake.addListener(
                change -> {
                    switch (change) {
                        case SnakeChangeListener.Change.Moved m -> {
                            Point2D transNewHead =
                                    m.newHead().wrappedAround(field.getWidth(), field.getHeight());
                            Point2D transPrevHead =
                                    m.prevHead().wrappedAround(field.getWidth(), field.getHeight());
                            Point2D transNewTail =
                                    m.newTail().wrappedAround(field.getWidth(), field.getHeight());
                            Point2D transPrevTail =
                                    m.prevTail().wrappedAround(field.getWidth(), field.getHeight());

                            available.add(transPrevTail);
                            available.remove(transNewHead);

                            field.set(transPrevTail, new TileState.Free());

                            // Check for collision
                            switch (field.get(transNewHead)) {
                                case TileState.OccupiedBySnake os -> {
                                    if (snake == playerSnake
                                            || (os.snake() == playerSnake)
                                                    && (os
                                                                    instanceof
                                                                    TileState.OccupiedBySnake
                                                                            .SnakeHead
                                                            || os
                                                                    instanceof
                                                                    TileState.OccupiedBySnake
                                                                            .SnakeHeadTail)) {
                                        state = State.GAME_OVER;
                                    } else {
                                        pendingToRemove.add(snake);
                                        for (Point2D p : snake.getSegments()) {
                                            Point2D transP =
                                                    p.wrappedAround(
                                                            field.getWidth(), field.getHeight());
                                            if (field.get(transP)
                                                            instanceof TileState.OccupiedBySnake
                                                    && ((TileState.OccupiedBySnake)
                                                                            field.get(transP))
                                                                    .snake()
                                                            == snake) {
                                                field.set(transP, new TileState.Free());
                                            }
                                        }
                                        return;
                                    }
                                }
                                case TileState.OccupiedByApple oa -> {
                                    oa.apple().apply(snake);
                                    spawnNewApple();
                                    if (snake == playerSnake) {
                                        score++;
                                    }
                                }
                                case TileState.Free free -> {}
                            }

                            if (snake.size() == 1) {
                                field.set(
                                        transNewHead,
                                        new TileState.OccupiedBySnake.SnakeHeadTail(snake, false));
                            } else {
                                field.set(
                                        transNewHead,
                                        new TileState.OccupiedBySnake.SnakeHead(
                                                snake, transPrevHead, false));

                                TileState.OccupiedBySnake.SnakeHead prevHead =
                                        (TileState.OccupiedBySnake.SnakeHead)
                                                field.get(transPrevHead);
                                field.set(
                                        transPrevHead,
                                        new TileState.OccupiedBySnake.SnakeBody(
                                                snake, prevHead.next(), transNewHead));

                                TileState.OccupiedBySnake newTail =
                                        (TileState.OccupiedBySnake) field.get(transNewTail);
                                if (!(newTail instanceof TileState.OccupiedBySnake.SnakeHead)) {
                                    field.set(
                                            transNewTail,
                                            new TileState.OccupiedBySnake.SnakeTail(
                                                    snake,
                                                    ((TileState.OccupiedBySnake.SnakeBody) newTail)
                                                            .prev()));
                                }
                            }
                        }
                        case SnakeChangeListener.Change.Growed g -> {
                            Point2D transNewTail =
                                    g.newTail().wrappedAround(field.getWidth(), field.getHeight());
                            Point2D transPrevTail =
                                    g.prevTail().wrappedAround(field.getWidth(), field.getHeight());

                            available.remove(transNewTail);

                            field.set(
                                    transNewTail,
                                    new TileState.OccupiedBySnake.SnakeTail(snake, transPrevTail));

                            if (snake.size() == 2) {
                                field.set(
                                        transPrevTail,
                                        new TileState.OccupiedBySnake.SnakeHead(
                                                snake, transNewTail, false));
                            } else {
                                TileState.OccupiedBySnake.SnakeTail prevTail =
                                        (TileState.OccupiedBySnake.SnakeTail)
                                                field.get(transPrevTail);
                                field.set(
                                        transPrevTail,
                                        new TileState.OccupiedBySnake.SnakeBody(
                                                snake, transNewTail, prevTail.prev()));
                            }
                        }
                        case SnakeChangeListener.Change.Shrinked s -> {}
                    }
                });

        snakes.add(snake);

        return snake;
    }

    public void setPlayerSnake(Snake snake) {
        playerSnake = snake;
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

    public Set<Point2D> getAvailable() {
        return available;
    }

    public void tick() {
        if (state == State.RUNNING) {
            for (Snake snake : snakes) {
                snake.tick();
                if (state == State.GAME_OVER) {
                    break;
                }
            }
            for (Snake s : pendingToRemove) {
                snakes.remove(s);
            }
            pendingToRemove.clear();
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
