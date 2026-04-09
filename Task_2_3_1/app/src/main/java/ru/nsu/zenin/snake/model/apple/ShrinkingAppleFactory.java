package ru.nsu.zenin.snake.model.apple;

import java.util.Set;
import ru.nsu.zenin.collection.Point2D;
import ru.nsu.zenin.util.Random;

public class ShrinkingAppleFactory extends AppleFactory {
    public Apple create(Set<Point2D> available) {
        return new ShrinkingApple(Random.getRandomFromSet(available));
    }
}
