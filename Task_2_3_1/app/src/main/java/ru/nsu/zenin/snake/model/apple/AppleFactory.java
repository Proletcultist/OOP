package ru.nsu.zenin.snake.model.apple;

import java.util.Set;
import ru.nsu.zenin.collection.Point2D;
import java.util.concurrent.ThreadLocalRandom;
import ru.nsu.zenin.snake.model.apple.exception.NoAvailablePointsException;

public abstract class AppleFactory {
    public abstract Apple create(Set<Point2D> available);

    protected Point2D getRandomPoint(Set<Point2D> available) {
        if (available.isEmpty()) {
            throw new NoAvailablePointsException("No points available for apple creation");
        }

        int rand = ThreadLocalRandom.current().nextInt(available.size());
        for (Point2D p : available) {
            if (rand-- == 0) {
                return p;
            }
        }

        throw new RuntimeException();
    }

    public AppleFactory combinedWith(AppleFactory other, double chance) {
        AppleFactory realThis = this;
        return new AppleFactory() {
            @Override
            public Apple create(Set<Point2D> available) {
                if (Math.random() > chance) {
                    return realThis.create(available);
                } else {
                    return other.create(available);
                }
            }
        };
    }
}
