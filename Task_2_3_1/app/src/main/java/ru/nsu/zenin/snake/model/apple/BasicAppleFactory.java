package ru.nsu.zenin.snake.model.apple;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.snake.model.apple.exception.NoAvailablePointsException;

public class BasicAppleFactory extends AppleFactory {
    public Apple create(Set<Point2D> available) {
        if (available.isEmpty()) {
            throw new NoAvailablePointsException("No points available for apple creation");
        }

        int rand = ThreadLocalRandom.current().nextInt(available.size());
        for (Point2D p : available) {
            if (rand-- == 0) {
                return new BasicApple(p);
            }
        }

        throw new RuntimeException();
    }
}
