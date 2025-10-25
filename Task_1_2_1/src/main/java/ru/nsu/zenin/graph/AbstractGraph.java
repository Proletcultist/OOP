package ru.nsu.zenin.graph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;
import ru.nsu.zenin.graph.exception.IdCollisionException;
import ru.nsu.zenin.graph.exception.NoSuchVertexException;
import ru.nsu.zenin.graph.parser.GraphParser;

public abstract class AbstractGraph<T extends Comparable<T>> implements Graph<T> {
    public void addSubgraphFromFile(
            Path file, GraphParser<T> parser, Function<String, T> labelParser)
            throws IOException, IdCollisionException, NoSuchVertexException {
        parser.addSubgraphFromFile(file, this, labelParser);
    }

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
                List<T> thisIdNeighbours = this.getVertexNeighbours(id);
                List<T> otherIdNeighbours = other.getVertexNeighbours(id);

                thisIdNeighbours.sort(
                        (L, R) -> {
                            return ((Comparable<T>) L).compareTo(R);
                        });
                otherIdNeighbours.sort(
                        (L, R) -> {
                            return ((Comparable<T>) L).compareTo(R);
                        });

                if (!thisIdNeighbours.equals(otherIdNeighbours)) {
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
        neighbours.forEach(
                Li -> {
                    Li.sort(
                            (L, R) -> {
                                return ((Comparable<T>) L).compareTo(R);
                            });
                });

        try {
            for (T id : vertexes) {
                neighbours.add(this.getVertexNeighbours(id));
            }
        } catch (NoSuchVertexException e) {
            throw new RuntimeException("Unexpected exception", e);
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

    @Override
    public abstract Graph<T> clone();
}
