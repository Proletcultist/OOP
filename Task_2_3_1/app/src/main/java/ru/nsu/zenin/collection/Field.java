package ru.nsu.zenin.collection;

import java.util.function.BiConsumer;

public interface Field<T> {
    void set(Point2D point, T value);

    T get(Point2D point);

    boolean contains(Point2D p);

    int getWidth();

    int getHeight();

    void setAll(T value);

    void resize(T fill, int width, int height);

    void forEach(BiConsumer<Point2D, T> action);
}
