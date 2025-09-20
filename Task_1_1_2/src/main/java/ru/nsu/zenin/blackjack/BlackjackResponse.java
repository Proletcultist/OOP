package ru.nsu.zenin.blackjack;

import ru.nsu.zenin.cardgame.Response;

class BlackjackResponse extends Response {
    private final boolean takeCard;

    BlackjackResponse(String msg, boolean takeCard) {
        super(msg);
        this.takeCard = takeCard;
    }

    boolean getTakeCard() {
        return takeCard;
    }
}
