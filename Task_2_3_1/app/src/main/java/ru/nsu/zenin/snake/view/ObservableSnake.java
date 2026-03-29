package ru.nsu.zenin.snake.view;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import ru.nsu.zenin.collection.Point2D;

public class ObservableSnake {
    private final Color color;
    private final ObservableList<Point2D> segments = FXCollections.observableArrayList();

    public ObservableSnake(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public ObservableList<Point2D> getSegments() {
        return this.segments;
    }

    public void addListener(SnakeChangeListener listener) {
        segments.addListener(
                (ListChangeListener<Point2D>)
                        change -> {
                            while (change.next()) {
                                if (change.wasAdded()) {
                                    for (Point2D p : change.getAddedSubList()) {
                                        listener.onChange(
                                                new SnakeChangeListener.Change(
                                                        this,
                                                        p,
                                                        SnakeChangeListener.Change.Action
                                                                .OCCUPIED));
                                    }
                                } else if (change.wasRemoved()) {
                                    for (Point2D p : change.getRemoved()) {
                                        listener.onChange(
                                                new SnakeChangeListener.Change(
                                                        this,
                                                        p,
                                                        SnakeChangeListener.Change.Action.LEFT));
                                    }
                                }
                            }
                        });
    }
}
