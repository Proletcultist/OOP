package ru.nsu.zenin.graph.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import ru.nsu.zenin.graph.Graph;
import ru.nsu.zenin.graph.exception.IdCollisionException;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;

/**
 * Parser for simple format of graph description. Format is following:
 *
 * <pre>
 * {@code <vertex> <vertex> ... <vertex>}
 * {@code <edge>}
 * ...
 * {@code <edge>}
 * </pre>
 *
 * <p>Here:
 *
 * <ul>
 *   <li>{@code <amount_of_vertexes>} - positive int
 *   <li>{@code <edge>} - pair of indexes of vertexes, separated with witespace. Indexes start from
 *       0
 *   <li>{@code <vertex>} - integer
 * </ul>
 *
 * <p>The only allowed encoding for the file is UTF-8.
 */
public class SimpleGraphParser<T> implements GraphParser<T> {

    // labelParser should assume, what string representation of label cannot contain whitespaces
    // inside
    public void addSubgraphFromFile(Path file, Graph<T> graph, Function<String, T> labelParser)
            throws IOException, IdCollisionException, NoSuchVertexException {
        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

            List<T> vertexes =
                    Arrays.stream(br.readLine().split("\\w+"))
                            .map(str -> labelParser.apply(str))
                            .collect(Collectors.toList());

            for (T id : vertexes) {
                graph.addVertex(id);
            }

            while (br.ready()) {
                String[] splited = br.readLine().split("\\w+");
                if (splited.length != 2) {
                    throw new InputMismatchException("Invalid edge format");
                }

                T fromId = labelParser.apply(splited[0]);
                T toId = labelParser.apply(splited[1]);

                graph.addEdge(fromId, toId);
            }
        }
    }
}
