package ru.nsu.zenin.snake.model.apple;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import ru.nsu.zenin.collection.Point2D;

public class BasicAppleFactory extends AppleFactory {
    public Apple create(Set<Point2D> available) {
        int rand = ThreadLocalRandom.current().nextInt(available.size());
        for (Point2D p : available) {
            if (rand-- == 0) {
                return new BasicApple(p);
            }
        }

        throw new RuntimeException();
    }
}
