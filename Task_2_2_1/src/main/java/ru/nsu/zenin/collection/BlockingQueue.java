package ru.nsu.zenin.collection;

public interface BlockingQueue<T> {
    void put(T elem);

    T take();
}
