package ru.nsu.zenin.graph;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.graph.exception.IdCollisionException;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;

class AbstractGraphTest {

    @Test
    void equalsTest() throws IdCollisionException, NoSuchVertexException {
        Graph<Integer> g1 = new AdjMatrixGraph<Integer>();
        Graph<Integer> g2 = new AdjListsGraph<Integer>();
        Graph<Integer> g3 = new AdjListsGraph<Integer>();

        g1.addVertex(1);
        g1.addVertex(2);
        g1.addVertex(3);

        g2.addVertex(1);
        g2.addVertex(2);
        g2.addVertex(3);

        g3.addVertex(1);
        g3.addVertex(2);
        g3.addVertex(3);

        g1.addEdge(1, 2);
        g1.addEdge(2, 3);
        g1.addEdge(2, 1);
        g1.addEdge(3, 1);

        g2.addEdge(3, 1);
        g2.addEdge(2, 1);
        g2.addEdge(2, 3);
        g2.addEdge(1, 2);

        g3.addEdge(1, 2);
        g3.addEdge(2, 3);

        Assertions.assertEquals(g1, g1);
        Assertions.assertEquals(g1, g2);
        Assertions.assertNotEquals(g1, g3);
    }

    @Test
    void hashTest() throws IdCollisionException, NoSuchVertexException {
        Graph<Integer> g1 = new AdjMatrixGraph<Integer>();
        Graph<Integer> g2 = new AdjListsGraph<Integer>();
        Graph<Integer> g3 = new AdjListsGraph<Integer>();

        g1.addVertex(1);
        g1.addVertex(2);
        g1.addVertex(3);

        g2.addVertex(1);
        g2.addVertex(2);
        g2.addVertex(3);

        g3.addVertex(1);
        g3.addVertex(2);
        g3.addVertex(3);

        g1.addEdge(1, 2);
        g1.addEdge(2, 3);
        g1.addEdge(2, 1);
        g1.addEdge(3, 1);

        g2.addEdge(3, 1);
        g2.addEdge(1, 2);
        g2.addEdge(2, 1);
        g2.addEdge(2, 3);

        g3.addEdge(1, 2);
        g3.addEdge(2, 3);

        Assertions.assertEquals(g1.hashCode(), g1.hashCode());
        Assertions.assertEquals(g1.hashCode(), g2.hashCode());
        Assertions.assertNotEquals(g1.hashCode(), g3.hashCode());
    }
}
