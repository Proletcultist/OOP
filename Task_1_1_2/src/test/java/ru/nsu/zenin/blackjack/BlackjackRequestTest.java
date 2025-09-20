package ru.nsu.zenin.blackjack;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.cardgame.exception.DriverException;

public class BlackjackRequestTest {

    @Test
    public void builderTest() {
        BlackjackHand dealerHand = new BlackjackHand(), playerHand = new BlackjackHand();

        BlackjackRequest req =
                BlackjackRequest.builder()
                        .dealerHand(dealerHand)
                        .playerHand(playerHand)
                        .msg("BBB")
                        .build();

        Assertions.assertEquals(req.getDealerHand(), dealerHand);
        Assertions.assertEquals(req.getPlayerHand(), playerHand);
        Assertions.assertEquals(req.getText(), "BBB");
    }

    @Test
    public void builderExceptionTest() {
        Assertions.assertThrows(
                DriverException.class,
                () -> {
                    BlackjackRequest.builder().build();
                });
    }
}
