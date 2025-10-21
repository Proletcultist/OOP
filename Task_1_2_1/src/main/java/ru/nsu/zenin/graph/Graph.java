package ru.nsu.zenin.graph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;
import ru.nsu.zenin.graph.exception.IdCollisionException;
import ru.nsu.zenin.graph.parser.GraphParser;

public interface Graph<T> {
    void addVertex(T id) throws IdCollisionException;

    void removeVertexById(T id);

    void addEdgeBetween(T from, T to);

    void removeEdgeBetween(T from, T to);

    List<T> getVertexNeighbours(T id);

    void addSubgraphFromFile(Path file, GraphParser<T> parser, Function<String, T> labelParser)
            throws IOException, IdCollisionException;

    int getVertexesAmount();

    /* Return sorted set of existing vertexes' ids.
     */
    Set<T> getVertexes();

    List<Pair<T, T>> getEdges();
}
