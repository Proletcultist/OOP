package ru.nsu.zenin.cardgame;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CardTest {

    @Test
    public void equalsEqualTest() {
        Card card1 = new Card(Card.Suit.CLUBS, Card.Rank.TWO);
        Card card2 = new Card(Card.Suit.CLUBS, Card.Rank.TWO);

        Assertions.assertEquals(card1, card2);
    }

    @Test
    public void equalsNotEqual() {
        Card card1 = new Card(Card.Suit.CLUBS, Card.Rank.THREE);
        Card card2 = new Card(Card.Suit.CLUBS, Card.Rank.TWO);

        Assertions.assertNotEquals(card1, card2);
    }

    @Test
    public void toStringTest() {
        Card card = new Card(Card.Suit.CLUBS, Card.Rank.THREE);
        String str = card.toString();
    }

    @Test
    public void getRankTest() {
        Card card = new Card(Card.Suit.CLUBS, Card.Rank.THREE);

        Assertions.assertEquals(card.getRank(), Card.Rank.THREE);
    }

    @Test
    public void getSuitTest() {
        Card card = new Card(Card.Suit.CLUBS, Card.Rank.THREE);

        Assertions.assertEquals(card.getSuit(), Card.Suit.CLUBS);
    }
}
