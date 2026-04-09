package ru.nsu.zenin.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Point2DTest {
    @Test
    void wrappingTest() {
        Point2D p = new Point2D(3, 3);

        Assertions.assertEquals(p.wrappedAround(2, 2), new Point2D(1, 1));
    }

    @Test
    void sidesTest() {
        Point2D p = new Point2D(3, 3);

        Assertions.assertTrue(p.isOnTheLeftOf(new Point2D(4, 3)));
        Assertions.assertTrue(p.isOnTheRightOf(new Point2D(2, 3)));
        Assertions.assertTrue(p.isOnTheTopOf(new Point2D(3, 4)));
        Assertions.assertTrue(p.isOnTheBottomOf(new Point2D(3, 2)));

        Assertions.assertFalse(p.isOnTheBottomOf(new Point2D(0, 2)));
    }
}
