package ru.nsu.zenin.collection;

public record Point2D(int x, int y) {
    public Point2D wrappedAround(int width, int height) {
        return new Point2D((x() % width + width) % width, (y() % height + height) % height);
    }
}
