package ru.nsu.zenin.collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class BlockingLinkedList<T> implements BlockingQueue<T> {

    private final Object PutLock = new Object(), TakeLock = new Object();

    private Node<T> supplementary;

    public BlockingLinkedList() {
        supplementary = new Node<T>(null);
        supplementary.setNext(supplementary);
        supplementary.setPrev(supplementary);
    }

    public void put(T elem) {
        synchronized (PutLock) {
            synchronized (supplementary.getNext()) {
                Node<T> thisNext = supplementary.getNext();
                Node<T> thisPrev = supplementary;

                Node<T> neww = new Node<T>(elem);
                neww.setNext(thisNext);
                neww.setPrev(thisPrev);

                thisNext.setPrev(neww);
                thisPrev.setNext(neww);

                // If it was the very first object in queue - notify any waiting taker
                if (thisNext == thisPrev) {
                    supplementary.getNext().notify();
                }
            }
        }
    }

    public T take() {
        synchronized (TakeLock) {
            synchronized (supplementary.getPrev()) {
                while (supplementary.getPrev() == supplementary) {
                    try {
                        supplementary.getPrev().wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                Node<T> thisPrev = supplementary.getPrev().getPrev();
                Node<T> thisNext = supplementary;

                T ret = supplementary.getPrev().getValue();

                thisPrev.setNext(thisNext);
                thisNext.setPrev(thisPrev);

                return ret;
            }
        }
    }

    @RequiredArgsConstructor
    private class Node<T> {
        @Getter private final T value;
        @Setter @Getter private Node<T> prev = null;
        @Setter @Getter private Node<T> next = null;
    }
}
