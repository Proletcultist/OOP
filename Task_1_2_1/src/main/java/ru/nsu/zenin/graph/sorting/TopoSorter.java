package ru.nsu.zenin.graph.sorting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import ru.nsu.zenin.graph.Graph;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;

public class TopoSorter {
    private TopoSorter() {}

    /**
     * @return null if graph cannot be toposorted
     */
    public static <T extends Comparable<T>> List<T> sort(Graph<T> g) {
        List<T> out = new ArrayList<T>();
        Graph<T> cloned = (Graph<T>) g.clone();

        Stack<T> noIncomingVerts = new Stack<T>();
        Map<T, AtomicInteger> incomingEdgesAmount = new HashMap<T, AtomicInteger>();
        Set<T> initialVertexes = cloned.getVertexes();

        try {
            for (T v : initialVertexes) {
                incomingEdgesAmount.put(v, new AtomicInteger(0));
            }

            for (T vFrom : initialVertexes) {
                for (T vTo : cloned.getVertexNeighbours(vFrom)) {
                    incomingEdgesAmount.get(vTo).incrementAndGet();
                }
            }

            for (T v : initialVertexes) {
                if (incomingEdgesAmount.get(v).get() == 0) {
                    noIncomingVerts.push(v);
                }
            }

            while (!noIncomingVerts.empty()) {
                T removed = noIncomingVerts.pop();
                out.add(removed);

                for (T v : cloned.getVertexNeighbours(removed)) {
                    if (incomingEdgesAmount.get(v).decrementAndGet() == 0) {
                        noIncomingVerts.push(v);
                    }
                }

                cloned.removeVertex(removed);
            }

            if (cloned.getVertexesAmount() != 0) {
                return null;
            } else {
                return out;
            }
        } catch (NoSuchVertexException e) {
            throw new RuntimeException("Unexpected exception occured", e);
        }
    }
}
