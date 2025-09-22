package ru.nsu.zenin.blackjack;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.cardgame.exception.DriverException;

public class BlackjackMessageTest {

    @Test
    public void builderTest() {

        BlackjackHand dealerHand = new BlackjackHand(), playerHand = new BlackjackHand();

        BlackjackMessage msg =
                BlackjackMessage.builder()
                        .type(BlackjackMessage.MessageType.DRAW)
                        .dealerHand(dealerHand)
                        .playerHand(playerHand)
                        .cardsInDeckLeft(12)
                        .isDealerEndedHisTurn(true)
                        .msg("AAAA")
                        .build();

        Assertions.assertEquals(msg.getType(), BlackjackMessage.MessageType.DRAW);
        Assertions.assertEquals(msg.getDealerHand(), dealerHand);
        Assertions.assertEquals(msg.getPlayerHand(), playerHand);
        Assertions.assertEquals(msg.getCardsInDeckLeft(), 12);
        Assertions.assertEquals(msg.getText(), "AAAA");
    }

    @Test
    public void builderExceptionTest() {
        Assertions.assertThrows(
                DriverException.class,
                () -> {
                    BlackjackMessage msg = BlackjackMessage.builder().build();
                });
    }
}
