package ru.nsu.zenin.blackjack;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BlackjackResponseTest {

    @Test
    public void constructorTest() {
        BlackjackResponse resp = new BlackjackResponse("AA", true);

        Assertions.assertTrue(resp.getTakeCard());
        Assertions.assertEquals(resp.getText(), "AA");
    }
}
