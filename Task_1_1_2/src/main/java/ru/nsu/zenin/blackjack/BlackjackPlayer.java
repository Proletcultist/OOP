package ru.nsu.zenin.blackjack;

import ru.nsu.zenin.cardgame.Hand;
import ru.nsu.zenin.cardgame.Player;

public class BlackjackPlayer implements Player {
    private int points = 0;
    private Hand hand = new Hand();

    public Hand getHand() {
        return hand;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int diff) {
        points += diff;
    }

    public void setPointToZero() {
        points = 0;
    }
}
