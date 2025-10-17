package ru.nsu.zenin.graph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import ru.nsu.zenin.graph.exception.NoSuchEdgeException;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;
import ru.nsu.zenin.graph.parser.GraphParser;

public class AdjMatrixGraph implements Graph {

    private LinkedList<LinkedList<Integer>> adjMatrix = new LinkedList<LinkedList<Integer>>();
    private Map<Integer, Integer> idToIndex = new TreeMap<Integer, Integer>();
    private TreeMap<Integer, Integer> indexToId = new TreeMap<Integer, Integer>();
    private int idCounter = 0;

    public int addVertex() {
        idToIndex.put(idCounter, adjMatrix.size());
        indexToId.put(adjMatrix.size(), idCounter);

        for (LinkedList row : adjMatrix) {
            row.add(0);
        }

        adjMatrix.add(new LinkedList<Integer>());

        for (int i = 0; i < getVertexesAmount(); i++) {
            adjMatrix.get(adjMatrix.size() - 1).add(0);
        }

        return idCounter++;
    }

    public void removeVertexById(int id) {
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

    public void addEdgeBetween(int from, int to) {
        int fromIndex, toIndex;

        try {
            fromIndex = idToIndex.get(from);
            toIndex = idToIndex.get(to);
        } catch (NullPointerException e) {
            throw new NoSuchVertexException("No vertex with such id in graph", e);
        }

        adjMatrix.get(fromIndex).set(toIndex, adjMatrix.get(fromIndex).get(toIndex) + 1);
    }

    public void removeEdgeBetween(int from, int to) {
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

    public List<Integer> getVertexNeighbours(int id) {
        int index;
        try {
            index = idToIndex.remove(id);
        } catch (NullPointerException e) {
            throw new NoSuchVertexException("No vertex with such id in graph", e);
        }

        List<Integer> out = new ArrayList<Integer>();

        for (int i = 0; i < getVertexesAmount(); i++) {
            if (adjMatrix.get(index).get(i) > 0) {
                out.add(indexToId.get(i));
            }
        }

        return out;
    }

    public void addSubgraphFromFile(Path file, GraphParser parser) throws IOException {
        parser.addSubgraphFromFile(file, this);
    }

    public int getVertexesAmount() {
        return adjMatrix.size();
    }

    @Override
    public String toString() {
        StringBuilder strBld = new StringBuilder();

        strBld.append("Vertexes: ");

        List<Integer> ids = new ArrayList<Integer>();
        idToIndex.forEach(
                (K, V) -> {
                    ids.add(K);
                });

        strBld.append(ids.toString() + "\n");

        strBld.append("Edges: ");

        ArrayList<ArrayList<Integer>> edges = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < getVertexesAmount(); i++) {
            for (int j = 0; j < getVertexesAmount(); j++) {
                for (int k = 0; k < adjMatrix.get(i).get(j); k++) {
                    ArrayList<Integer> edge = new ArrayList<Integer>();
                    edge.add(indexToId.get(i));
                    edge.add(indexToId.get(j));
                    edges.add(edge);
                }
            }
        }

        strBld.append(edges.toString() + "\n");

        return strBld.toString();
    }
}
