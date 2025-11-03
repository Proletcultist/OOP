package ru.nsu.zenin.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import ru.nsu.zenin.graph.exception.IdCollisionException;
import ru.nsu.zenin.graph.exception.NoSuchEdgeException;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;

public class AdjListsGraph<T extends Comparable<T>> extends AbstractGraph<T> {

    private Map<T, LinkedList<T>> vertexesNeighbours = new TreeMap<T, LinkedList<T>>();

    public void addVertex(T id) throws Exception {
        if (vertexesNeighbours.containsKey(id)) {
            throw new IdCollisionException("Vertex with such id already exists");
        }

        vertexesNeighbours.put(id, new LinkedList<T>());
    }

    public void removeVertex(T id) throws Exception {
        if (!vertexesNeighbours.containsKey(id)) {
            throw new NoSuchVertexException("No vertex with such id in graph");
        }

        vertexesNeighbours.forEach(
                (K, V) -> {
                    V.remove(id);
                });

        vertexesNeighbours.remove(id);
    }

    public void addEdge(T from, T to) throws Exception {
        if (!vertexesNeighbours.containsKey(from) || !vertexesNeighbours.containsKey(to)) {
            throw new NoSuchVertexException("No vertex with such id in graph");
        }

        vertexesNeighbours.get(from).add(to);
    }

    public void removeEdge(T from, T to) throws Exception {
        if (!vertexesNeighbours.containsKey(from) || !vertexesNeighbours.containsKey(to)) {
            throw new NoSuchVertexException("No vertex with such id in graph");
        }

        if (!vertexesNeighbours.get(from).remove(to)) {
            throw new NoSuchEdgeException("No such edge in graph");
        }
    }

    public List<T> getVertexNeighbours(T id) throws Exception {
        if (!vertexesNeighbours.containsKey(id)) {
            throw new NoSuchVertexException("No vertex with such id in graph");
        }

        return (List<T>) vertexesNeighbours.get(id).clone();
    }

    public int getVertexesAmount() {
        return vertexesNeighbours.size();
    }

    /* Return sorted set of existing vertexes' ids.
     */
    public Set<T> getVertexes() {
        return vertexesNeighbours.keySet().stream().collect(Collectors.toSet());
    }

    public List<Pair<T, T>> getEdges() {
        List<Pair<T, T>> out = new ArrayList<Pair<T, T>>();

        vertexesNeighbours.forEach(
                (from, neighb) -> {
                    for (T to : neighb) {
                        out.add(Pair.of(from, to));
                    }
                });
        return out;
    }

    public Graph<T> clone() {
        Graph<T> New = new AdjListsGraph<T>();

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
}
