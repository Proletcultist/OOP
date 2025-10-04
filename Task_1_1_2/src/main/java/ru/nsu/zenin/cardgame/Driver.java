package ru.nsu.zenin.cardgame;

public interface Driver {
    void initializeGame(Game game);

    void step();

    void initializeNextRound();
}
