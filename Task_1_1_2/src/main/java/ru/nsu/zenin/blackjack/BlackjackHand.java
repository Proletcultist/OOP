package ru.nsu.zenin.blackjack;

import ru.nsu.zenin.cardgame.Card;
import ru.nsu.zenin.cardgame.Hand;
import ru.nsu.zenin.cardgame.exception.DriverException;

class BlackjackHand extends Hand {

    private int points = 0;
    private int hiddenCardsCount = 0;

    @Override
    public void addCard(Card card) {
        if (!(card instanceof BlackjackCard)) {
            throw new DriverException(
                    "Tried to add to BlackjackHand card which isn't BlackjackCard");
        }

        ((BlackjackCard) card).addOwnerHand(this);
        points += ((BlackjackCard) card).getPoints();
        super.addCard(card);
    }

    @Override
    public void addCards(Card... cards) {
        for (int i = 0; i < cards.length; i++) {
            if (!(cards[i] instanceof BlackjackCard)) {
                throw new DriverException(
                        "Tried to add to BlackjackHand card which isn't BlackjackCard");
            }

            ((BlackjackCard) cards[i]).addOwnerHand(this);
            points += ((BlackjackCard) cards[i]).getPoints();
            super.addCard(cards[i]);
        }
    }

    @Override
    public Card removeCard(int index) {
        BlackjackCard removedCard = (BlackjackCard) super.removeCard(index);
        points -= removedCard.getPoints();
        if (removedCard.isHidden()) {
            hiddenCardsCount--;
        }
        removedCard.clear();

        return removedCard;
    }

    int getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return super.toString() + (hasHiddenCards() ? "" : String.format(" => %d", points));
    }

    void hideCard(int index) {
        BlackjackCard card = (BlackjackCard) super.getCard(index);
        if (!card.isHidden()) {
            hiddenCardsCount++;
        }

        card.hide();
    }

    void unhideCard(int index) {
        BlackjackCard card = (BlackjackCard) super.getCard(index);
        if (card.isHidden()) {
            hiddenCardsCount--;
        }

        card.unhide();
    }

    boolean hasHiddenCards() {
        return hiddenCardsCount > 0;
    }
}
