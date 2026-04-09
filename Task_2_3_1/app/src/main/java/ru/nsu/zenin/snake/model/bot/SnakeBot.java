package ru.nsu.zenin.snake.model.bot;

import lombok.RequiredArgsConstructor;
import ru.nsu.zenin.snake.model.Snake;

@RequiredArgsConstructor
public abstract class SnakeBot {
    protected final Snake snake;

    public abstract void tick();
}
