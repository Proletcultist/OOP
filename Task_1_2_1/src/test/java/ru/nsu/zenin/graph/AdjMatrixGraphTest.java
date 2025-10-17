package ru.nsu.zenin.graph;

import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AdjMatrixGraphTest {

    @Test
    void test() {
        Graph g = new AdjMatrixGraph();

        int a = g.addVertex();
        int b = g.addVertex();

        g.addEdgeBetween(a, b);

        ArrayList<Integer> aNeighb = new ArrayList<Integer>();
        aNeighb.add(b);

        Assertions.assertEquals(g.getVertexesAmount(), 2);
        Assertions.assertEquals(g.getVertexNeighbours(a), aNeighb);
        Assertions.assertEquals(g.getVertexNeighbours(b), new ArrayList());
    }
}
