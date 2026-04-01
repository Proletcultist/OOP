package ru.nsu.zenin.collection;

import java.util.List;
import java.util.ArrayList;

public class ObservableField<T> implements Field<T> {
    private final List<List<T>> state;
    private int width, height;

    private final List<FieldChangeListener<T>> listeners;

    public ObservableField(T initialValue, int width, int height) {
        this.width = width;
        this.height = height;
        this.state = new ArrayList<List<T>>(height);
        for (int i = 0; i < height; i++) {
            state.add(new ArrayList<T>(width));
            for (int j = 0; j < width; j++) {
                state.get(i).add(initialValue);
            }
        }

        this.listeners = new ArrayList<FieldChangeListener<T>>();
    }

    public void addListener(FieldChangeListener<T> listener) {
        listeners.add(listener);
    }

    public void set(Point2D point, T value) {
        set(point.x(), point.y(), value);
    }

    public T get(Point2D point) {
        return get(point.x(), point.y());
    }

    public void set(int x, int y, T value) {
        state.get(y).set(x, value);
        for (FieldChangeListener<T> l : listeners) {
            l.onChange(new FieldChangeListener.Change<T>(new Point2D(x, y), value));
        }
    }

    public T get(int x, int y) {
        return state.get(y).get(x);
    }

    public boolean contains(Point2D p) {
        return p.x() < width && p.y() < height && p.x() >= 0 && p.y() >= 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void resize(T fill, int width, int height) {
        while (height > this.height) {
            state.add(new ArrayList<T>(width));
            this.height++;
        }
        while (height < this.height) {
            state.remove(this.height - 1);
            this.height--;
        }

        this.width = width;

        for (List<T> l : state) {
            while (l.size() > this.width) {
                l.remove(l.size() - 1);
            }
            while (l.size() < this.width) {
                l.add(fill);
            }
        }
    }
}
