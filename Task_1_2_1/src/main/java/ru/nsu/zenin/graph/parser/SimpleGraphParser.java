package ru.nsu.zenin.graph.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import ru.nsu.zenin.graph.Graph;

/**
 * Parser for simple format of graph description. Format is following: <amount_of_vertexes> <edge>
 * ... <edge>
 *
 * <p>Here: - <amount_of_vertexes> - positive int - <edge> - pair of indexes of vertexes, separated
 * with witespace. Indexes start from 0
 *
 * <p>The only allowed encoding for the file is UTF-8.
 */
public class SimpleGraphParser implements GraphParser {

    private List<Integer> indexToId = new ArrayList<Integer>();

    public void addSubgraphFromFile(Path file, Graph graph) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

            int vertexesAmount = Integer.valueOf(br.readLine());

            for (int i = 0; i < vertexesAmount; i++) {
                indexToId.add(graph.addVertex());
            }

            while (br.ready()) {
                String[] splited = br.readLine().split("\\w+");
                if (splited.length != 2) {
                    throw new InputMismatchException("Invalid edge format");
                }

                int fromIndex = Integer.valueOf(splited[0]);
                int toIndex = Integer.valueOf(splited[1]);

                graph.addEdgeBetween(indexToId.get(fromIndex), indexToId.get(toIndex));
            }
        }
    }
}
