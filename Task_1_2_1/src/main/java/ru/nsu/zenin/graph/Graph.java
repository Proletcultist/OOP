package ru.nsu.zenin.graph;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;
import ru.nsu.zenin.graph.parser.GraphParser;

public interface Graph<T extends Comparable<T>> {
    void addVertex(T id) throws Exception;

    void removeVertex(T id) throws Exception;

    void addEdge(T from, T to) throws Exception;

    void removeEdge(T from, T to) throws Exception;

    List<T> getVertexNeighbours(T id) throws Exception;

    void addSubgraphFromFile(Path file, GraphParser<T> parser, Function<String, T> labelParser)
            throws Exception;

    int getVertexesAmount();

    /** Return sorted set of existing vertexes' ids. */
    Set<T> getVertexes();

    List<Pair<T, T>> getEdges();

    public Graph clone();
}
