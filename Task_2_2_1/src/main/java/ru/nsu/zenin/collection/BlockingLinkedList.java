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
            synchronized (supplementary.getPrev()) {
                Node<T> thisNext = supplementary;
                Node<T> thisPrev = supplementary.getPrev();

                Node<T> neww = new Node<T>(elem);
                neww.setNext(thisNext);
                neww.setPrev(thisPrev);

                thisNext.setPrev(neww);
                thisPrev.setNext(neww);

                // If it was the very first object in queue - notify any waiting taker
                if (thisNext == thisPrev) {
                    thisPrev.notify();
                }
            }
        }
    }

    public T take() throws InterruptedException {
        synchronized (TakeLock) {
            synchronized (supplementary.getNext()) {
                while (supplementary.getNext() == supplementary) {
                    supplementary.getNext().wait();
                }

                Node<T> thisPrev = supplementary;
                Node<T> thisNext = supplementary.getNext().getNext();

                T ret = supplementary.getNext().getValue();

                thisNext.setPrev(thisPrev);
                thisPrev.setNext(thisNext);

                return ret;
            }
        }
    }

    public T poll() {
        synchronized (TakeLock) {
            synchronized (supplementary.getPrev()) {
                if (supplementary.getPrev() == supplementary) {
                    return null;
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
