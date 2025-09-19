package ru.nsu.zenin.blackjack;

import static ru.nsu.zenin.cardgame.Card.Rank.*;
import static ru.nsu.zenin.cardgame.Card.Suit.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.cardgame.Card;
import ru.nsu.zenin.cardgame.exception.DriverException;

public class BlackjackCardTest {

    @Test
    public void hideUnhideTest() {
        BlackjackCard card = new BlackjackCard(new Card(HEARTS, TWO));

        Assertions.assertFalse(card.isHidden());

        card.unhide();

        Assertions.assertFalse(card.isHidden());

        card.hide();

        Assertions.assertTrue(card.isHidden());
    }

    @Test
    public void toStringTest() {
        BlackjackCard card = new BlackjackCard(new Card(HEARTS, TWO));

        String res = card.toString();
    }

    @Test
    public void getPointsWithoutOwnerTest() {
        BlackjackCard card = new BlackjackCard(new Card(HEARTS, TWO));

        Assertions.assertThrows(
                DriverException.class,
                () -> {
                    card.getPoints();
                });
    }
}
