package ru.nsu.zenin.graph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import ru.nsu.zenin.graph.parser.GraphParser;

public interface Graph {
    int addVertex();

    void removeVertexById(int id);

    void addEdgeBetween(int from, int to);

    void removeEdgeBetween(int from, int to);

    List<Integer> getVertexNeighbours(int id);

    void addSubgraphFromFile(Path file, GraphParser parser) throws IOException;

    int getVertexesAmount();
}
