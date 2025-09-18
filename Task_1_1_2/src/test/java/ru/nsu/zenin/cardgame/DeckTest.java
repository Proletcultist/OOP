package ru.nsu.zenin.cardgame;

import static ru.nsu.zenin.cardgame.Card.Rank.*;
import static ru.nsu.zenin.cardgame.Card.Suit.*;

import java.util.Stack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DeckTest {

    @Test
    public void deckTest() {

        Deck deck = new Deck(new Card(CLUBS, TWO), new Card(HEARTS, ACE), new Card(DIAMONDS, KING));

        Stack<Card> deck_cards = new Stack<Card>();

        while (!deck.isEmpty()) {
            deck_cards.push(deck.getTop());
        }

        Assertions.assertArrayEquals(
                deck_cards.toArray(),
                new Card[] {new Card(DIAMONDS, KING), new Card(HEARTS, ACE), new Card(CLUBS, TWO)});
    }

    @Test
    public void putGetDeckTest() {
        Deck deck = new Deck();

        deck.putOnTop(new Card(DIAMONDS, QUEEN));

        Assertions.assertEquals(deck.getTop(), new Card(DIAMONDS, QUEEN));
    }

    @Test
    public void deckSizeTest() {
        Deck deck = new Deck(new Card(CLUBS, TWO), new Card(HEARTS, ACE), new Card(DIAMONDS, KING));

        Assertions.assertEquals(deck.size(), 3);

        Assertions.assertFalse(deck.isEmpty());
    }

    @Test
    public void convertionTest() {
        Deck deck = new Deck(new Card(HEARTS, FIVE));

        deck.convertAllCards(card -> new TestCard(card));

        Assertions.assertEquals(((TestCard) deck.getTop()).getField(), 101);
    }

    private class TestCard extends Card {
        private int field = 101;

        public TestCard(Card card) {
            super(card.getSuit(), card.getRank());
        }

        public int getField() {
            return field;
        }
    }
}
