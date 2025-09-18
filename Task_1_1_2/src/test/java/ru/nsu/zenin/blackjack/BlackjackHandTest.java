package ru.nsu.zenin.blackjack;

import static ru.nsu.zenin.cardgame.Card.Rank.*;
import static ru.nsu.zenin.cardgame.Card.Suit.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.cardgame.Card;
import ru.nsu.zenin.cardgame.exception.DriverException;

public class BlackjackHandTest {

    @Test
    public void hideUnhideTest() {
        BlackjackHand hand = new BlackjackHand();

        hand.addCard(new BlackjackCard(new Card(HEARTS, TWO)));

        Assertions.assertFalse(hand.hasHiddenCards());

        hand.unhideCard(0);

        Assertions.assertFalse(hand.hasHiddenCards());

        hand.hideCard(0);

        Assertions.assertTrue(hand.hasHiddenCards());

        hand.hideCard(0);

        Assertions.assertTrue(hand.hasHiddenCards());

        hand.unhideCard(0);

        Assertions.assertFalse(hand.hasHiddenCards());
    }

    @Test
    public void toStringTest() {
        BlackjackHand hand = new BlackjackHand();

        hand.addCard(new BlackjackCard(new Card(HEARTS, TWO)));

        String res = hand.toString();

        hand.hideCard(0);

        res = hand.toString();
    }

    @Test
    public void addRemoveCardTest() {
        BlackjackHand hand = new BlackjackHand();

        final BlackjackCard card = new BlackjackCard(new Card(HEARTS, TWO));

        Assertions.assertThrows(
                DriverException.class,
                () -> {
                    card.getPoints();
                });

        hand.addCard(card);

        Assertions.assertEquals(card.getPoints(), 2);

        BlackjackCard removedCard = ((BlackjackCard) hand.removeCard(0));

        Assertions.assertEquals(removedCard, card);

        Assertions.assertThrows(
                DriverException.class,
                () -> {
                    card.getPoints();
                });
    }

    @Test
    public void aceTest() {
        BlackjackHand hand = new BlackjackHand();

        hand.addCards(
                new BlackjackCard(new Card(DIAMONDS, ACE)),
                new BlackjackCard(new Card(DIAMONDS, ACE)));

        Assertions.assertEquals(hand.getPoints(), 12);
    }

    @Test
    public void addingNotBlackjackCardTest() {
        BlackjackHand hand = new BlackjackHand();

        Assertions.assertThrows(
                DriverException.class,
                () -> {
                    hand.addCard(new Card(HEARTS, THREE));
                });

        Assertions.assertThrows(
                DriverException.class,
                () -> {
                    hand.addCards(new Card(HEARTS, THREE), new Card(DIAMONDS, FOUR));
                });
    }
}
