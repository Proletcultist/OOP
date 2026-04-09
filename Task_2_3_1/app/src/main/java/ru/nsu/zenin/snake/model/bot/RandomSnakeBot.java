package ru.nsu.zenin.snake.model.bot;

import ru.nsu.zenin.snake.model.Snake;
import java.util.concurrent.ThreadLocalRandom;

public class RandomSnakeBot extends SnakeBot {
    private static final double CHANGE_DIRECTION_CHANCE = 0.01;
    private static final Snake.Direction[] OPTIONS = {Snake.Direction.UP, Snake.Direction.DOWN, Snake.Direction.LEFT, Snake.Direction.RIGHT};
    
    private int currentDirectionIndex = 0;

    public RandomSnakeBot(Snake snake) {
        super(snake);
        Snake.Direction currDir = snake.getLastMoveDirection();
        for (int i = 0; i < OPTIONS.length; i++) {
            if (OPTIONS[i] == currDir) {
                currentDirectionIndex = i;
                break;
            }
        }
    }

    public void tick() {
        if (ThreadLocalRandom.current().nextDouble() < CHANGE_DIRECTION_CHANCE) {
            int nextDir = ThreadLocalRandom.current().nextInt(OPTIONS.length - 1);
            currentDirectionIndex = (currentDirectionIndex + nextDir) % OPTIONS.length;
            snake.setPendingDirection(OPTIONS[currentDirectionIndex]);
        }
    }
}
