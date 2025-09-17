package ru.nsu.zenin.blackjack;

import static ru.nsu.zenin.cardgame.Card.Rank.*;

import ru.nsu.zenin.cardgame.Card;
import ru.nsu.zenin.cardgame.exception.DriverException;

public class BlackjackCard extends Card {

    private boolean isHidden = false;
    private BlackjackPlayer owner;
    private int points;

    public BlackjackCard(Card card) {
        super(card.getSuit(), card.getRank());
    }

    public BlackjackCard withOwner(BlackjackPlayer owner) {
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

        owner.addPoints(points);
        return this;
    }

    public BlackjackCard cleared() {
        this.owner = null;
        this.isHidden = false;
        return this;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void hide() {
        isHidden = true;
    }

    public void unhide() {
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
}
