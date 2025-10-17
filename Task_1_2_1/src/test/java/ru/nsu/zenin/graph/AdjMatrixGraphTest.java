package ru.nsu.zenin.graph;

import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.graph.exception.IdCollisionException;

class AdjMatrixGraphTest {

    @Test
    void test() throws IdCollisionException {
        Graph<Integer> g = new AdjMatrixGraph<Integer>();

        g.addVertex(1);
        g.addVertex(2);

        g.addEdgeBetween(1, 2);

        ArrayList<Integer> aNeighb = new ArrayList<Integer>();
        aNeighb.add(2);

        Assertions.assertEquals(g.getVertexesAmount(), 2);
        Assertions.assertEquals(g.getVertexNeighbours(1), aNeighb);
        Assertions.assertEquals(g.getVertexNeighbours(2), new ArrayList());
    }

    /*
       @Test
       void equalsSameTypesTest() {
           Graph g1 = new AdjMatrixGraph();
           Graph g2 = new AdjMatrixGraph();

    int a1 = g1.addVertex();
    int b1 = g1.addVertex();

    int a2 = g2.addVertex();
    int b2 = g2.addVertex();

    g1.addEdgeBetween(a1, b1);
       }
       */
}
