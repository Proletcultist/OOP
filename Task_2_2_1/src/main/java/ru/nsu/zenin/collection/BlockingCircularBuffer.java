package ru.nsu.zenin.collection;

import org.apache.commons.lang3.mutable.MutableInt;

public class BlockingCircularBuffer<T> implements BlockingQueue<T> {
    private T[] buffer;
    private MutableInt write_index = new MutableInt(0), read_index = new MutableInt(0);

    public BlockingCircularBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Buffer capacity cannot be non-positive");
        }
        buffer = (T[]) new Object[capacity + 1];
    }

    public void put(T elem) throws InterruptedException {
        synchronized (write_index) {
            while ((write_index.intValue() + 1) % buffer.length == read_index.intValue()) {
                write_index.wait();
            }

            buffer[write_index.intValue()] = elem;

            write_index.setValue((write_index.intValue() + 1) % buffer.length);
        }

        synchronized (read_index) {
            read_index.notify();
        }
    }

    public T take() throws InterruptedException {
        T ret;

        synchronized (read_index) {
            while (read_index.equals(write_index)) {
                read_index.wait();
            }

            ret = buffer[read_index.intValue()];
            buffer[read_index.intValue()] = null;

            read_index.setValue((read_index.intValue() + 1) % buffer.length);
        }

        synchronized (write_index) {
            write_index.notify();
        }

        return ret;
    }

    public T poll() {
        T ret;

        synchronized (read_index) {
            if (read_index.equals(write_index)) {
                return null;
            }

            ret = buffer[read_index.intValue()];
            buffer[read_index.intValue()] = null;

            read_index.setValue((read_index.intValue() + 1) % buffer.length);
        }

        synchronized (write_index) {
            write_index.notify();
        }

        return ret;
    }
}
