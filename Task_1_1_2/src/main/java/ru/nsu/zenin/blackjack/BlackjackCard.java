package ru.nsu.zenin.blackjack;

import static ru.nsu.zenin.cardgame.Card.Rank.*;

import ru.nsu.zenin.cardgame.Card;
import ru.nsu.zenin.cardgame.exception.DriverException;

class BlackjackCard extends Card {

    private boolean isHidden = false;
    private BlackjackHand owner = null;
    private int points = 0;

    public BlackjackCard(Card card) {
        super(card.getSuit(), card.getRank());
    }

    void addOwnerHand(BlackjackHand owner) {
        this.owner = owner;

        points =
                switch (this.getRank()) {
                    case TWO -> 2;
                    case THREE -> 3;
                    case FOUR -> 4;
                    case FIVE -> 5;
                    case SIX -> 6;
                    case SEVEN -> 7;
                    case EIGHT -> 8;
                    case NINE -> 9;
                    case TEN -> 10;
                    case JACK -> 10;
                    case QUEEN -> 10;
                    case KING -> 10;
                    case ACE -> owner.getPoints() + 11 > 21 ? 1 : 11;
                    default ->
                            throw new DriverException(
                                    String.format(
                                            "Illegal card rank \"%s\" for blackjack",
                                            this.getRank().toString()));
                };
    }

    void clear() {
        this.owner = null;
        this.isHidden = false;
        this.points = 0;
    }

    boolean isHidden() {
        return isHidden;
    }

    void hide() {
        isHidden = true;
    }

    void unhide() {
        isHidden = false;
    }

    @Override
    public String toString() {
        if (isHidden) {
            return "<Hidden card>";
        } else {
            return super.toString() + (owner == null ? "" : String.format(" (%d)", points));
        }
    }

    int getPoints() {
        return points;
    }
}
