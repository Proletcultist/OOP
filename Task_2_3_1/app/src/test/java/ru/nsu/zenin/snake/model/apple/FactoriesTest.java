package ru.nsu.zenin.snake.model.apple;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.collection.Point2D;

public class FactoriesTest {
    @Test
    void basicFactoryTest() {
        AppleFactory fact = new BasicAppleFactory();
        Set<Point2D> set = new HashSet<Point2D>();
        set.add(new Point2D(0, 0));
        Apple apple = fact.create(set);

        Assertions.assertTrue(apple instanceof BasicApple);
        Assertions.assertEquals(apple.getPosition(), new Point2D(0, 0));
    }

    @Test
    void shrinkFactoryTest() {
        AppleFactory fact = new ShrinkingAppleFactory();
        Set<Point2D> set = new HashSet<Point2D>();
        set.add(new Point2D(0, 0));
        Apple apple = fact.create(set);

        Assertions.assertTrue(apple instanceof ShrinkingApple);
        Assertions.assertEquals(apple.getPosition(), new Point2D(0, 0));
    }
}
