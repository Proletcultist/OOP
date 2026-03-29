package ru.nsu.zenin.snake.model;

import java.util.Set;
import ru.nsu.zenin.collection.Point2D;

public abstract class AppleFactory {
    public abstract Apple create(Set<Point2D> available);

    public AppleFactory combinedWith(AppleFactory other, double chance) {
        return new AppleFactory() {
            @Override
            public Apple create(Set<Point2D> available) {
                if (Math.random() < chance) {
                    return this.create(available);
                } else {
                    return other.create(available);
                }
            }
        };
    }
}
