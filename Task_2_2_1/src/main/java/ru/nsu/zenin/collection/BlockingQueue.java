package ru.nsu.zenin.collection;

public interface BlockingQueue<T> {
    void put(T elem) throws InterruptedException;

    T take() throws InterruptedException;

    T poll();
}
