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

    public void put(T elem) {
        synchronized (write_index) {
            while ((write_index.intValue() + 1) % buffer.length == read_index.intValue()) {
                try {
                    write_index.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            buffer[write_index.intValue()] = elem;

            write_index.setValue((write_index.intValue() + 1) % buffer.length);
        }

        synchronized (read_index) {
            read_index.notify();
        }
    }

    public T take() {
        T ret;

        synchronized (read_index) {
            while (read_index.equals(write_index)) {
                try {
                    read_index.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
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
