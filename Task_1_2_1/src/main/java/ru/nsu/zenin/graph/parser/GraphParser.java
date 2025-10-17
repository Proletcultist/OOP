package ru.nsu.zenin.graph.parser;

import java.io.IOException;
import java.nio.file.Path;
import ru.nsu.zenin.graph.Graph;

public interface GraphParser {
    void addSubgraphFromFile(Path file, Graph graph) throws IOException;
}
