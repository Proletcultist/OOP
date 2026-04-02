package ru.nsu.zenin.collection;

public interface Field<T> {
    void set(Point2D point, T value);

    T get(Point2D point);

    void set(int x, int y, T value);

    T get(int x, int y);

    boolean contains(Point2D p);

    int getWidth();

    int getHeight();

    void resize(T fill, int width, int height);
}
