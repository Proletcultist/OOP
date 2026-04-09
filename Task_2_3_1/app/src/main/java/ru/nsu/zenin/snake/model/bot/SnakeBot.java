package ru.nsu.zenin.snake.model.bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.nsu.zenin.collection.Field;
import ru.nsu.zenin.snake.model.Snake;
import ru.nsu.zenin.snake.model.TileState;

@RequiredArgsConstructor
public abstract class SnakeBot {
    @Getter protected final Snake snake;
    protected final Field<TileState> field;

    public abstract void tick();
}
