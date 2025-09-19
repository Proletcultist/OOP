package ru.nsu.zenin.cardgame;

public interface Driver {
    public void initializeGame(Game game);

    public void step();

    public void initializeNextRound();
}
