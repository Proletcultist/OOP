package ru.nsu.zenin.graph.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.BiConsumer;

public class LinkedListMatrixColumnIterator<T> {

    private final ArrayList<ListIterator<T>> iterators;

    public LinkedListMatrixColumnIterator(LinkedList<LinkedList<T>> mtx) {
        iterators = new ArrayList<ListIterator<T>>(mtx.size());

        for (int i = 0; i < mtx.size(); i++) {
            iterators.add(mtx.get(i).listIterator(0));
        }
    }

    public boolean hasNext() {
        return iterators.size() > 0 && iterators.get(0).hasNext();
    }

    public void next() {
        for (int i = 0; i < iterators.size(); i++) {
            iterators.get(i).next();
        }
    }

    /** Iterate forth and apply specified action to next column of elements. */
    public void next(BiConsumer<T, Integer> action) {
        for (int i = 0; i < iterators.size(); i++) {
            action.accept(iterators.get(i).next(), i);
        }
    }

    public void remove() {
        for (int i = 0; i < iterators.size(); i++) {
            iterators.get(i).remove();
        }
    }
}
