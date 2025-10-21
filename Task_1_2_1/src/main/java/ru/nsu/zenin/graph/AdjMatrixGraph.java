package ru.nsu.zenin.graph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;
import ru.nsu.zenin.graph.exception.IdCollisionException;
import ru.nsu.zenin.graph.exception.NoSuchEdgeException;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;
import ru.nsu.zenin.graph.parser.GraphParser;

public class AdjMatrixGraph<T> extends AbstractGraph<T> {

    private LinkedList<LinkedList<Integer>> adjMatrix = new LinkedList<LinkedList<Integer>>();
    private Map<T, Integer> idToIndex = new TreeMap<T, Integer>();
    private TreeMap<Integer, T> indexToId = new TreeMap<Integer, T>();

    public void addVertex(T id) throws IdCollisionException {
        if (idToIndex.containsKey(id)) {
            throw new IdCollisionException("Vertex with such id already exists");
        }
        idToIndex.put(id, adjMatrix.size());
        indexToId.put(adjMatrix.size(), id);

        for (LinkedList row : adjMatrix) {
            row.add(0);
        }

        adjMatrix.add(new LinkedList<Integer>());

        for (int i = 0; i < getVertexesAmount(); i++) {
            adjMatrix.get(adjMatrix.size() - 1).add(0);
        }
    }

    public void removeVertexById(T id) throws NoSuchVertexException {
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
                        V--;
                    }
                });

        indexToId
                .tailMap(index, false)
                .forEach(
                        (K, V) -> {
                            K--;
                        });
    }

    public void addEdgeBetween(T from, T to) throws NoSuchVertexException {
        int fromIndex, toIndex;

        try {
            fromIndex = idToIndex.get(from);
            toIndex = idToIndex.get(to);
        } catch (NullPointerException e) {
            throw new NoSuchVertexException("No vertex with such id in graph", e);
        }

        adjMatrix.get(fromIndex).set(toIndex, adjMatrix.get(fromIndex).get(toIndex) + 1);
    }

    public void removeEdgeBetween(T from, T to) throws NoSuchVertexException, NoSuchEdgeException {
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
            index = idToIndex.remove(id);
        } catch (NullPointerException e) {
            throw new NoSuchVertexException("No vertex with such id in graph", e);
        }

        List<T> out = new ArrayList<T>();

        for (int i = 0; i < getVertexesAmount(); i++) {
            if (adjMatrix.get(index).get(i) > 0) {
                out.add(indexToId.get(i));
            }
        }

        return out;
    }

    public void addSubgraphFromFile(
            Path file, GraphParser<T> parser, Function<String, T> labelParser)
            throws IOException, IdCollisionException, NoSuchVertexException {
        parser.addSubgraphFromFile(file, this, labelParser);
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

        for (int i = 0; i < getVertexesAmount(); i++) {
            for (int j = 0; j < getVertexesAmount(); j++) {
                for (int k = 0; k < adjMatrix.get(i).get(j); k++) {
                    edges.add(Pair.of(indexToId.get(i), indexToId.get(j)));
                }
            }
        }

        return edges;
    }
}
