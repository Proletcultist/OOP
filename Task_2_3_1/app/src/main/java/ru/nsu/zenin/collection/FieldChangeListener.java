package ru.nsu.zenin.collection;

@FunctionalInterface
public interface FieldChangeListener<T> {
    void onChange(Change c);

    public record Change<T>(Point2D point, T state) {}
}
