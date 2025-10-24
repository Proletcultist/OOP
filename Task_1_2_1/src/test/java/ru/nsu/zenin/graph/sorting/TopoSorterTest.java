package ru.nsu.zenin.graph.sorting;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.graph.AdjListsGraph;
import ru.nsu.zenin.graph.AdjMatrixGraph;
import ru.nsu.zenin.graph.Graph;
import ru.nsu.zenin.graph.IncMatrixGraph;
import ru.nsu.zenin.graph.exception.IdCollisionException;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;

class TopoSorterTest {

    @Test
    void test1() throws IdCollisionException, NoSuchVertexException {
        Graph<Integer> g = new AdjMatrixGraph<Integer>();

        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);

        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(2, 3);

        List<Integer> sorted = new ArrayList<Integer>();
        sorted.add(1);
        sorted.add(2);
        sorted.add(3);

        Assertions.assertEquals(TopoSorter.sort(g), sorted);
    }

    @Test
    void test2() throws IdCollisionException, NoSuchVertexException {
        Graph<Integer> g = new AdjListsGraph<Integer>();

        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);

        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(2, 3);

        List<Integer> sorted = new ArrayList<Integer>();
        sorted.add(1);
        sorted.add(2);
        sorted.add(3);

        Assertions.assertEquals(TopoSorter.sort(g), sorted);
    }

    @Test
    void test3() throws IdCollisionException, NoSuchVertexException {
        Graph<Integer> g = new IncMatrixGraph<Integer>();

        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);

        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(2, 3);

        List<Integer> sorted = new ArrayList<Integer>();
        sorted.add(1);
        sorted.add(2);
        sorted.add(3);

        Assertions.assertEquals(TopoSorter.sort(g), sorted);
    }
}
