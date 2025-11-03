package ru.nsu.zenin.graph.parser;

import java.nio.file.Path;
import java.util.function.Function;
import ru.nsu.zenin.graph.Graph;

public interface GraphParser<T extends Comparable<T>> {
    void addSubgraphFromFile(Path file, Graph<T> graph, Function<String, T> labelParser)
            throws Exception;
}
