package ru.nsu.zenin.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang3.tuple.Pair;
import ru.nsu.zenin.graph.exception.IdCollisionException;
import ru.nsu.zenin.graph.exception.NoSuchEdgeException;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;

public class AdjMatrixGraph<T extends Comparable<T>> extends AbstractGraph<T> {

    private LinkedList<LinkedList<Integer>> adjMatrix = new LinkedList<LinkedList<Integer>>();
    private Map<T, Integer> idToIndex = new TreeMap<T, Integer>();
    private LinkedList<T> indexToId = new LinkedList<T>();

    public void addVertex(T id) throws IdCollisionException {
        if (idToIndex.containsKey(id)) {
            throw new IdCollisionException("Vertex with such id already exists");
        }
        idToIndex.put(id, adjMatrix.size());
        indexToId.add(id);

        for (LinkedList row : adjMatrix) {
            row.add(0);
        }

        adjMatrix.add(new LinkedList<Integer>());

        for (int i = 0; i < getVertexesAmount(); i++) {
            adjMatrix.get(getVertexesAmount() - 1).add(0);
        }
    }

    public void removeVertex(T id) throws NoSuchVertexException {
        int index;
        try {
            index = idToIndex.remove(id);
        } catch (NullPointerException e) {
            throw new NoSuchVertexException("No vertex with such id in graph", e);
        }

        for (LinkedList row : adjMatrix) {
            row.remove(index);
        }

        indexToId.remove(index);
        adjMatrix.remove(index);

        idToIndex.forEach(
                (K, V) -> {
                    if (V > index) {
                        idToIndex.put(K, V - 1);
                    }
                });
    }

    public void addEdge(T from, T to) throws NoSuchVertexException {
        int fromIndex, toIndex;

        try {
            fromIndex = idToIndex.get(from);
            toIndex = idToIndex.get(to);
        } catch (NullPointerException e) {
            throw new NoSuchVertexException("No vertex with such id in graph", e);
        }

        adjMatrix.get(fromIndex).set(toIndex, adjMatrix.get(fromIndex).get(toIndex) + 1);
    }

    public void removeEdge(T from, T to) throws NoSuchVertexException, NoSuchEdgeException {
        int fromIndex, toIndex;

        try {
            fromIndex = idToIndex.get(from);
            toIndex = idToIndex.get(to);
        } catch (NullPointerException e) {
            throw new NoSuchVertexException("No vertex with such id in graph", e);
        }

        if (adjMatrix.get(fromIndex).get(toIndex) == 0) {
            throw new NoSuchEdgeException("No vertex between this vertexes in graph");
        }

        adjMatrix.get(fromIndex).set(toIndex, adjMatrix.get(fromIndex).get(toIndex) - 1);
    }

    public List<T> getVertexNeighbours(T id) throws NoSuchVertexException {
        int index;
        try {
            index = idToIndex.get(id);
        } catch (NullPointerException e) {
            throw new NoSuchVertexException("No vertex with such id in graph", e);
        }

        List<T> out = new ArrayList<T>();

        ListIterator<Integer> it = adjMatrix.get(index).listIterator(0);
        for (int i = 0; i < getVertexesAmount(); i++) {
            if (it.next() > 0) {
                out.add(indexToId.get(i));
            }
        }

        return out;
    }

    public int getVertexesAmount() {
        return adjMatrix.size();
    }

    public Set<T> getVertexes() {
        Set<T> vertexes = new TreeSet<T>();
        idToIndex.forEach(
                (K, V) -> {
                    vertexes.add(K);
                });
        return vertexes;
    }

    public List<Pair<T, T>> getEdges() {
        List<Pair<T, T>> edges = new ArrayList<Pair<T, T>>();

        ListIterator<LinkedList<Integer>> rowIt = adjMatrix.listIterator(0);
        for (int i = 0; i < getVertexesAmount(); i++) {
            ListIterator<Integer> it = rowIt.next().listIterator(0);
            for (int j = 0; j < getVertexesAmount(); j++) {
                int edgesAmount = it.next();
                for (int k = 0; k < edgesAmount; k++) {
                    edges.add(Pair.of(indexToId.get(i), indexToId.get(j)));
                }
            }
        }

        return edges;
    }

    public Graph<T> clone() {
        Graph<T> New = new AdjMatrixGraph<T>();

        try {
            for (T v : this.getVertexes()) {
                New.addVertex(v);
            }

            for (Pair<T, T> e : this.getEdges()) {
                New.addEdge(e.getLeft(), e.getRight());
            }
        } catch (IdCollisionException e) {
            throw new RuntimeException("Unexpected exception occured", e);
        } catch (NoSuchVertexException e) {
            throw new RuntimeException("Unexpected exception occured", e);
        }

        return New;
    }
}
