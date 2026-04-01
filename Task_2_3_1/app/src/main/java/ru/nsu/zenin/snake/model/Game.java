package ru.nsu.zenin.snake.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.collection.Field;

public class Game {
    private final Field<TileState> field;
    private final Set<Point2D> available;
    private AppleFactory appleFactory = null;
    private final List<Snake> snakes = new ArrayList<Snake>();

    public Game(Field<TileState> field) {
        this.field = field;
        this.available = new HashSet<Point2D>();

        for (int i = 0; i < field.getWidth(); i++) {
            for (int j = 0; j < field.getHeight(); j++) {
                if (field.get(i, j) instanceof TileState.Free) {
                    available.add(new Point2D(i, j));
                }
            }
        }
    }

    public Snake createSnake(Point2D head, Snake.Direction dir, Integer ticksToMove) {
        ObservableList<Point2D> segments = FXCollections.observableArrayList();
        segments.add(head);
        Snake snake = new Snake(segments, dir, ticksToMove);

        available.remove(head);

        segments.addListener((ListChangeListener<Point2D>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Point2D segment : change.getAddedSubList()) {
                        if (field.contains(segment)) {
                            available.remove(segment);
                            field.set(segment, new TileState.OccupiedBySnake(snake));
                        }
                    }
                }
                else if (change.wasRemoved()) {
                    for (Point2D segment : change.getRemoved()) {
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

    public void setAppleFactory(AppleFactory factory) {
        this.appleFactory = factory;
    }

    public void tick() {
        for (Snake snake : snakes) {
            snake.tick();
        }
    }
}
