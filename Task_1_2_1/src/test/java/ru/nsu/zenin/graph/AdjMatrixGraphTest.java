package ru.nsu.zenin.graph;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeSet;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.graph.exception.IdCollisionException;
import ru.nsu.zenin.graph.exception.NoSuchEdgeException;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;
import ru.nsu.zenin.graph.parser.SimpleGraphParser;

class AdjMatrixGraphTest {

    @Test
    void neighboursTest() throws Exception {
        Graph<Integer> g = new AdjMatrixGraph<Integer>();

        g.addVertex(1);
        g.addVertex(2);

        g.addEdge(1, 2);

        ArrayList<Integer> aNeighb = new ArrayList<Integer>();
        aNeighb.add(2);

        Assertions.assertEquals(g.getVertexesAmount(), 2);
        Assertions.assertEquals(g.getVertexNeighbours(1), aNeighb);
        Assertions.assertEquals(g.getVertexNeighbours(2), new ArrayList());
    }

    @Test
    void removingTest() throws Exception {
        Graph<String> g = new AdjMatrixGraph<String>();

        g.addVertex("first");
        g.addVertex("second");

        g.addEdge("first", "second");

        TreeSet<String> vertexesBeforeRemoving = new TreeSet<String>();
        ArrayList<Pair<String, String>> edgesBeforeRemoving = new ArrayList<Pair<String, String>>();

        vertexesBeforeRemoving.add("first");
        vertexesBeforeRemoving.add("second");
        edgesBeforeRemoving.add(Pair.of("first", "second"));

        TreeSet<String> vertexesAfterRemoving = new TreeSet<String>();

        vertexesAfterRemoving.add("first");

        Assertions.assertEquals(g.getVertexes(), vertexesBeforeRemoving);
        Assertions.assertEquals(g.getEdges(), edgesBeforeRemoving);

        g.removeEdge("first", "second");

        Assertions.assertEquals(g.getVertexes(), vertexesBeforeRemoving);
        Assertions.assertEquals(g.getEdges(), new ArrayList<Pair<String, String>>());

        Assertions.assertThrows(
                NoSuchEdgeException.class,
                () -> {
                    g.removeEdge("first", "second");
                });

        g.removeVertex("second");

        Assertions.assertEquals(g.getVertexes(), vertexesAfterRemoving);

        Assertions.assertThrows(
                NoSuchVertexException.class,
                () -> {
                    g.removeVertex("second");
                });
    }

    @Test
    void removingTest2() throws Exception {
        Graph<String> g = new AdjMatrixGraph<String>();

        g.addVertex("first");
        g.addVertex("second");

        g.addEdge("first", "second");

        TreeSet<String> vertexesBeforeRemoving = new TreeSet<String>();
        ArrayList<Pair<String, String>> edgesBeforeRemoving = new ArrayList<Pair<String, String>>();

        vertexesBeforeRemoving.add("first");
        vertexesBeforeRemoving.add("second");
        edgesBeforeRemoving.add(Pair.of("first", "second"));

        TreeSet<String> vertexesAfterRemoving = new TreeSet<String>();

        vertexesAfterRemoving.add("second");

        Assertions.assertEquals(g.getVertexes(), vertexesBeforeRemoving);
        Assertions.assertEquals(g.getEdges(), edgesBeforeRemoving);

        g.removeVertex("first");

        Assertions.assertThrows(
                NoSuchVertexException.class,
                () -> {
                    g.removeEdge("first", "second");
                });

        Assertions.assertEquals(g.getVertexes(), vertexesAfterRemoving);
        Assertions.assertEquals(g.getEdges(), new ArrayList<Pair<String, String>>());

        Assertions.assertThrows(
                NoSuchVertexException.class,
                () -> {
                    g.removeVertex("first");
                });
    }

    @Test
    void idClashingTest() throws Exception {
        Graph<Character> g = new AdjMatrixGraph<Character>();

        g.addVertex('A');

        Assertions.assertThrows(
                IdCollisionException.class,
                () -> {
                    g.addVertex('A');
                });
    }

    @Test
    void invalidEdgeAddingTest() throws Exception {
        Graph<Character> g = new AdjMatrixGraph<Character>();

        g.addVertex('A');

        Assertions.assertThrows(
                NoSuchVertexException.class,
                () -> {
                    g.addEdge('A', 'B');
                });

        Assertions.assertThrows(
                NoSuchVertexException.class,
                () -> {
                    g.removeEdge('A', 'B');
                });
    }

    @Test
    void unexistingVertexNeighbTest() {
        Graph<Character> g = new AdjMatrixGraph<Character>();

        Assertions.assertThrows(
                NoSuchVertexException.class,
                () -> {
                    g.getVertexNeighbours('A');
                });
    }

    @Test
    void parsingTest() throws Exception {
        Graph<String> g = new AdjMatrixGraph<String>();

        g.addSubgraphFromFile(
                Paths.get(AdjMatrixGraphTest.class.getResource("/test_graph").toURI()),
                new SimpleGraphParser(),
                str -> str);

        Graph<String> expected = new AdjMatrixGraph<String>();

        expected.addVertex("A");
        expected.addVertex("B");
        expected.addVertex("C");
        expected.addVertex("D");

        expected.addEdge("B", "C");
        expected.addEdge("D", "A");
        expected.addEdge("C", "D");
        expected.addEdge("A", "B");

        Assertions.assertEquals(g, expected);

        Graph<String> g2 = new AdjMatrixGraph<String>();

        Assertions.assertThrows(
                NoSuchVertexException.class,
                () -> {
                    g2.addSubgraphFromFile(
                            Paths.get(
                                    AdjMatrixGraphTest.class
                                            .getResource("/test_graph_invalid")
                                            .toURI()),
                            new SimpleGraphParser(),
                            str -> str);
                });
    }

    @Test
    void toStringTest() throws Exception {
        Graph<Integer> g = new AdjMatrixGraph<Integer>();

        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);

        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(2, 3);

        Assertions.assertEquals(g.toString(), "Vertexes: [1, 2, 3]\nEdges: [(1,2), (2,3), (2,3)]");
    }
}
