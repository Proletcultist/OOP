package ru.nsu.zenin.collection;

public record Point2D(int x, int y) {
    public Point2D wrappedAround(int width, int height) {
        return new Point2D((x() % width + width) % width, (y() % height + height) % height);
    }
    
    public boolean isOnTheLeftOf(Point2D other) {
        return this.x() + 1 == other.x() && this.y() == other.y();
    }

    public boolean isOnTheRightOf(Point2D other) {
        return this.x() - 1 == other.x() && this.y() == other.y();
    }

    public boolean isOnTheTopOf(Point2D other) {
        return this.x() == other.x() && this.y() + 1 == other.y();
    }

    public boolean isOnTheBottomOf(Point2D other) {
        return this.x() == other.x() && this.y() - 1 == other.y();
    }
}
