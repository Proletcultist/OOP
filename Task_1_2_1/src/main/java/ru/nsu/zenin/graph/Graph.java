package ru.nsu.zenin.graph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;
import ru.nsu.zenin.graph.exception.IdCollisionException;
import ru.nsu.zenin.graph.exception.NoSuchEdgeException;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;
import ru.nsu.zenin.graph.parser.GraphParser;

public interface Graph<T> {
    void addVertex(T id) throws IdCollisionException;

    void removeVertex(T id) throws NoSuchVertexException;

    void addEdge(T from, T to) throws NoSuchVertexException;

    void removeEdge(T from, T to) throws NoSuchVertexException, NoSuchEdgeException;

    List<T> getVertexNeighbours(T id) throws NoSuchVertexException;

    void addSubgraphFromFile(Path file, GraphParser<T> parser, Function<String, T> labelParser)
            throws IOException, IdCollisionException, NoSuchVertexException;

    int getVertexesAmount();

    /** Return sorted set of existing vertexes' ids.
     */
    Set<T> getVertexes();

    List<Pair<T, T>> getEdges();

    public Graph clone();
}
