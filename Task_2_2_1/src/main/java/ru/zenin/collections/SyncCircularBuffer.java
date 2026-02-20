package ru.nsu.zenin.collections;

import org.apache.commons.lang3.mutable.MutableInt;

public class SyncCircularBuffer<T> {
    private T[] buffer;
    private MutableInt write_index = new MutableInt(0), read_index = new MutableInt(0);

    public SyncCircularBuffer(int capacity) {
        buffer = (T[]) new Object[capacity];
    }

    public synchronized void put(T elem) {
        synchronized (write_index) {
            while ((write_index.intValue() + 1) % buffer.length == read_index.intValue()) {
                try {
                    write_index.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        buffer[write_index.intValue()] = elem;

        write_index.setValue((write_index.intValue() + 1) % buffer.length);

        synchronized (read_index) {
            read_index.notify();
        }
    }

    public synchronized T take() {
        synchronized (read_index) {
            while (read_index.equals(write_index)) {
                try {
                    read_index.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        T ret = buffer[read_index.intValue()];
        buffer[read_index.intValue()] = null;

        read_index.setValue((read_index.intValue() + 1) % buffer.length);

        synchronized (write_index) {
            write_index.notify();
        }

        return ret;
    }
}
