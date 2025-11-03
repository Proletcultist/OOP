package ru.nsu.zenin.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.tuple.Pair;
import ru.nsu.zenin.graph.exception.IdCollisionException;
import ru.nsu.zenin.graph.exception.NoSuchEdgeException;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;
import ru.nsu.zenin.graph.util.ListMatrixColumnIterator;

public class IncMatrixGraph<T extends Comparable<T>> extends AbstractGraph<T> {

    private List<List<Incidence>> incMatrix = new LinkedList<List<Incidence>>();
    private Map<T, Integer> idToIndex = new TreeMap<T, Integer>();
    private List<T> indexToId = new LinkedList<T>();

    int edgesAmount = 0;

    public void addVertex(T id) throws Exception {
        if (idToIndex.containsKey(id)) {
            throw new IdCollisionException("Vertex with such id already exists");
        }
        idToIndex.put(id, incMatrix.size());
        indexToId.add(id);

        incMatrix.add(new LinkedList<Incidence>());

        for (int i = 0; i < edgesAmount; i++) {
            incMatrix.get(incMatrix.size() - 1).add(Incidence.NOT_INCIDENT);
        }
    }

    public void removeVertex(T id) throws Exception {
        int index;
        try {
            index = idToIndex.get(id);
        } catch (NullPointerException e) {
            throw new NoSuchVertexException("No vertex with such id in graph", e);
        }

        ListMatrixColumnIterator<Incidence> columnIt =
                new ListMatrixColumnIterator<Incidence>(incMatrix);

        while (columnIt.hasNext()) {
            AtomicBoolean remove = new AtomicBoolean(false);

            columnIt.next(
                    (inc, ind) -> {
                        if (ind == index && inc != Incidence.NOT_INCIDENT) {
                            remove.set(true);
                        }
                    });
            if (remove.get()) {
                columnIt.remove();
                edgesAmount--;
            }
        }

        idToIndex.remove(id);
        indexToId.remove(index);
        incMatrix.remove(index);

        idToIndex.forEach(
                (K, V) -> {
                    if (V > index) {
                        idToIndex.put(K, V - 1);
                    }
                });
    }

    public void addEdge(T from, T to) throws Exception {
        int fromIndex, toIndex;

        try {
            fromIndex = idToIndex.get(from);
            toIndex = idToIndex.get(to);
        } catch (NullPointerException e) {
            throw new NoSuchVertexException("No vertex with such id in graph", e);
        }

        for (List<Incidence> inc : incMatrix) {
            inc.add(Incidence.NOT_INCIDENT);
        }

        edgesAmount++;

        incMatrix.get(fromIndex).set(edgesAmount - 1, Incidence.INCIDENT_AS_START);
        incMatrix.get(toIndex).set(edgesAmount - 1, Incidence.INCIDENT_AS_END);
    }

    public void removeEdge(T from, T to) throws Exception {
        int fromIndex, toIndex;

        try {
            fromIndex = idToIndex.get(from);
            toIndex = idToIndex.get(to);
        } catch (NullPointerException e) {
            throw new NoSuchVertexException("No vertex with such id in graph", e);
        }

        ListMatrixColumnIterator<Incidence> columnIt =
                new ListMatrixColumnIterator<Incidence>(incMatrix);

        while (columnIt.hasNext()) {
            AtomicBoolean startAtFrom = new AtomicBoolean(false),
                    endsAtTo = new AtomicBoolean(false);
            columnIt.next(
                    (inc, ind) -> {
                        if (ind == fromIndex && inc == Incidence.INCIDENT_AS_START) {
                            startAtFrom.set(true);
                        }
                        if (ind == toIndex && inc == Incidence.INCIDENT_AS_END) {
                            endsAtTo.set(true);
                        }
                    });
            if (startAtFrom.get() && endsAtTo.get()) {
                columnIt.remove();
                edgesAmount--;
                return;
            }
        }

        throw new NoSuchEdgeException("No such edge in graph");
    }

    public List<T> getVertexNeighbours(T id) throws Exception {
        int index;
        try {
            index = idToIndex.get(id);
        } catch (NullPointerException e) {
            throw new NoSuchVertexException("No vertex with such id in graph", e);
        }

        List<T> out = new ArrayList<T>();
        ListMatrixColumnIterator<Incidence> columnIt =
                new ListMatrixColumnIterator<Incidence>(incMatrix);

        while (columnIt.hasNext()) {
            AtomicBoolean startsAtIndex = new AtomicBoolean(false);
            AtomicInteger endIndex = new AtomicInteger(0);
            columnIt.next(
                    (inc, ind) -> {
                        if (ind == index && inc == Incidence.INCIDENT_AS_START) {
                            startsAtIndex.set(true);
                        }
                        if (inc == Incidence.INCIDENT_AS_END) {
                            endIndex.set(ind);
                        }
                    });
            if (startsAtIndex.get()) {
                out.add(indexToId.get(endIndex.get()));
            }
        }

        return out;
    }

    public int getVertexesAmount() {
        return incMatrix.size();
    }

    /* Return sorted set of existing vertexes' ids.
     */
    public Set<T> getVertexes() {
        Set<T> vertexes = new TreeSet<T>();
        idToIndex.forEach(
                (K, V) -> {
                    vertexes.add(K);
                });
        return vertexes;
    }

    public List<Pair<T, T>> getEdges() {
        List<Pair<T, T>> out = new ArrayList<Pair<T, T>>();
        ListMatrixColumnIterator<Incidence> columnIt =
                new ListMatrixColumnIterator<Incidence>(incMatrix);

        while (columnIt.hasNext()) {
            AtomicInteger start = new AtomicInteger(0), end = new AtomicInteger(0);
            columnIt.next(
                    (inc, ind) -> {
                        if (inc == Incidence.INCIDENT_AS_START) {
                            start.set(ind);
                        }
                        if (inc == Incidence.INCIDENT_AS_END) {
                            end.set(ind);
                        }
                    });
            out.add(Pair.of(indexToId.get(start.get()), indexToId.get(end.get())));
        }

        return out;
    }

    public Graph<T> clone() {
        Graph<T> New = new IncMatrixGraph<T>();

        try {
            for (T v : this.getVertexes()) {
                New.addVertex(v);
            }

            for (Pair<T, T> e : this.getEdges()) {
                New.addEdge(e.getLeft(), e.getRight());
            }
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception occured", e);
        }
        return New;
    }

    private enum Incidence {
        INCIDENT_AS_START,
        INCIDENT_AS_END,
        NOT_INCIDENT
    }
}
