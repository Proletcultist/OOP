package ru.nsu.zenin.snake.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import ru.nsu.zenin.collection.Field;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.apple.AppleFactory;
import ru.nsu.zenin.snake.model.apple.Apple;

public class Game {
    private final Field<TileState> field;
    private final Set<Point2D> available;
    private final AppleFactory appleFactory;
    private final List<Snake> snakes = new ArrayList<Snake>();

    public Game(Field<TileState> field, AppleFactory appleFactory, int applesAmount) {
        this.field = field;
        this.appleFactory = appleFactory;
        this.available = new HashSet<Point2D>();

        field.forEach((point, state) -> {
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
                                    for (int i = change.getFrom(); i < change.getTo(); i++) {
                                        Point2D segment =
                                                change.getList()
                                                        .get(i)
                                                        .wrappedAround(
                                                                field.getWidth(),
                                                                field.getHeight());

                                        // Check for collision
                                        if (!available.contains(segment)) {
                                            switch (field.get(segment)) {
                                                case TileState.Free free -> {}
                                                case TileState.OccupiedBySnake os -> {/* TODO: Game over*/}
                                                case TileState.OccupiedByApple oa -> {
                                                    oa.apple().apply(snake);
                                                    field.set(
                                                            segment, new TileState.OccupiedBySnake(snake));
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
                                        segment =
                                                segment.wrappedAround(
                                                        field.getWidth(), field.getHeight());
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

    private void spawnNewApple() {
        Apple apple = appleFactory.create(available);
        available.remove(apple.getPosition());
        field.set(apple.getPosition(), new TileState.OccupiedByApple(apple));
    }

    public void tick() {
        for (Snake snake : snakes) {
            snake.tick();
        }
    }
}
