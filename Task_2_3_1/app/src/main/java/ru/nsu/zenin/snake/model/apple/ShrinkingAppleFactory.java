package ru.nsu.zenin.snake.model.apple;

import java.util.Set;
import ru.nsu.zenin.collection.Point2D;

public class ShrinkingAppleFactory extends AppleFactory {
    public Apple create(Set<Point2D> available) {
        return new ShrinkingApple(getRandomPoint(available));
    }
}
