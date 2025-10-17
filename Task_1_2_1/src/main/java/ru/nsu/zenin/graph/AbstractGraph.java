package ru.nsu.zenin.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;

public abstract class AbstractGraph<T> implements Graph<T> {
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Graph)) {
            return false;
        }

        Graph<T> other = (Graph<T>) obj;

        Set<T> thisVertexes = this.getVertexes();
        Set<T> otherVertexes = other.getVertexes();

        if (!thisVertexes.equals(otherVertexes)) {
            return false;
        }

        for (T id : thisVertexes) {
            try {
                if (!this.getVertexNeighbours(id).equals(other.getVertexNeighbours(id))) {
                    return false;
                }
            } catch (NoSuchVertexException e) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        Set<T> vertexes = this.getVertexes();
        ArrayList<List<T>> neighbours = new ArrayList<List<T>>();

        for (T id : vertexes) {
            neighbours.add(this.getVertexNeighbours(id));
        }

        return Objects.hash(vertexes, neighbours);
    }

    @Override
    public String toString() {
        StringBuilder strBld = new StringBuilder();

        strBld.append("Vertexes: ");

        Set<T> vertexes = this.getVertexes();

        strBld.append(vertexes.toString() + "\n");

        strBld.append("Edges: ");

        List<Pair<T, T>> edges = this.getEdges();

        strBld.append(edges.toString() + "\n");

        return strBld.toString();
    }
}
