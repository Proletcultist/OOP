package ru.nsu.zenin.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Point2DTest {
    @Test
    void wrappingTest() {
        Point2D p = new Point2D(3, 3);

        Assertions.assertEquals(p.wrappedAround(2, 2), new Point2D(1, 1));
    }
}
