package ru.nsu.zenin.blackjack;

import static ru.nsu.zenin.cardgame.Card.Rank.*;

import java.util.function.Function;
import ru.nsu.zenin.cardgame.Card;
import ru.nsu.zenin.cardgame.Card.Rank;
import ru.nsu.zenin.cardgame.exception.DriverException;

public class BlackjackCard extends Card {

    private boolean isHidden;
    private final Function<Integer, Integer> getPoints;

    public BlackjackCard(Card card, boolean isHidden) {
        super(card.getSuit(), card.getRank());

        this.isHidden = isHidden;
        this.getPoints = rankToGetPointsFunc(card.getRank());
    }

    public int getPoints(int currScore) {
        return this.getPoints.apply(currScore);
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
            return super.toString() + String.format(" (%d)", points);
        }
    }

    private Function<Integer, Integer> rankToGetPointsFunc(Rank rank) {
        return switch (rank) {
            case TWO -> x -> 2;
            case THREE -> x -> 3;
            case FOUR -> x -> 4;
            case FIVE -> x -> 5;
            case SIX -> x -> 6;
            case SEVEN -> x -> 7;
            case EIGHT -> x -> 8;
            case NINE -> x -> 9;
            case TEN -> x -> 10;
            case JACK -> x -> 10;
            case QUEEN -> x -> 10;
            case KING -> x -> 10;
            case ACE -> currScore -> currScore + 11 > 21 ? 1 : 1;
            default ->
                    throw new DriverException(
                            String.format(
                                    "Illegal card rank \"%s\" for blackjack", rank.toString()));
        };
    }
}
