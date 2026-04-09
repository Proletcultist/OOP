package ru.nsu.zenin.snake.model.bot;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import ru.nsu.zenin.snake.model.Snake;
import ru.nsu.zenin.snake.model.TileState;
import ru.nsu.zenin.collection.Field;

@RequiredArgsConstructor
public abstract class SnakeBot {
    @Getter protected final Snake snake;
    protected final Field<TileState> field;

    public abstract void tick();
}
