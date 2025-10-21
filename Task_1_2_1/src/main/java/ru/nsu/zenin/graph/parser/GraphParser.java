package ru.nsu.zenin.graph.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;
import ru.nsu.zenin.graph.Graph;
import ru.nsu.zenin.graph.exception.IdCollisionException;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;

public interface GraphParser<T> {
    void addSubgraphFromFile(Path file, Graph<T> graph, Function<String, T> labelParser)
            throws IOException, IdCollisionException, NoSuchVertexException;
}
