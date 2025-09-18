package ru.nsu.zenin.cardgame;

import static ru.nsu.zenin.cardgame.Card.Rank.*;
import static ru.nsu.zenin.cardgame.Card.Suit.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HandTest {

    @Test
    public void handSizeTest() {
        Hand hand = new Hand();

        hand.addCard(new Card(CLUBS, TWO));

        hand.addCards(new Card(HEARTS, QUEEN), new Card(DIAMONDS, THREE));

        Assertions.assertEquals(hand.size(), 3);

        Assertions.assertThrows(
                IndexOutOfBoundsException.class,
                () -> {
                    hand.getCard(3);
                });
        Assertions.assertThrows(
                IndexOutOfBoundsException.class,
                () -> {
                    hand.removeCard(100);
                });

        hand.removeCard(0);

        Assertions.assertEquals(hand.size(), 2);
    }

    @Test
    public void gettingRemovingTest() {

        Hand hand = new Hand();

        hand.addCard(new Card(CLUBS, TWO));

        Assertions.assertEquals(hand.getCard(0), new Card(CLUBS, TWO));
        Assertions.assertEquals(hand.removeCard(0), new Card(CLUBS, TWO));
    }

    @Test
    public void toStringTest() {
        Hand hand = new Hand();

        hand.addCard(new Card(CLUBS, TWO));

        String res = hand.toString();
    }

    @Test
    public void streamTest() {
        Hand hand = new Hand();

        hand.addCard(new Card(CLUBS, TWO));

        hand.addCards(new Card(HEARTS, QUEEN), new Card(DIAMONDS, THREE));
        Assertions.assertEquals(
                hand.stream()
                        .map(
                                card ->
                                        switch (card.getRank()) {
                                            case TWO -> 2;
                                            case QUEEN -> 4;
                                            case THREE -> 3;
                                            default -> 10;
                                        })
                        .reduce((a, b) -> a + b)
                        .orElse(0),
                9);
    }
}
