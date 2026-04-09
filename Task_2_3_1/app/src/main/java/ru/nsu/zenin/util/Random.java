package ru.nsu.zenin.util;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Random {
    public static <T> T getRandomFromSet(Set<T> set) {
        if (set.isEmpty()) {
            throw new IllegalArgumentException("Cannot get random element from empty set");
        }

        int rand = ThreadLocalRandom.current().nextInt(set.size());
        for (T x : set) {
            if (rand-- == 0) {
                return x;
            }
        }

        throw new RuntimeException("Unreachable statement is reached");
    }
}
