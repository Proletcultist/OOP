package ru.nsu.zenin.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.HashSet;

public class RandomTest {
    @Test
    void test() {
        Set<Integer> set = new HashSet<Integer>();
        set.add(1);

        Assertions.assertEquals(Random.getRandomFromSet(set), 1);
    }

    @Test
    void test2() {
        Set<Integer> set = new HashSet<Integer>();
        set.add(1);
        set.add(2);
        set.add(3);

        Assertions.assertTrue(set.contains(Random.getRandomFromSet(set)));
    }

    @Test
    void testThrows() {
        Set<Integer> set = new HashSet<Integer>();

        Assertions.assertThrows(IllegalArgumentException.class, () -> Random.getRandomFromSet(set));
    }
}
