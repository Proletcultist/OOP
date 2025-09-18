package ru.nsu.zenin.cardgame;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageTest {

    @Test
    void messageTest() {
        Message msg = new Message("ABOBA");

        Assertions.assertEquals(msg.getText(), "ABOBA");
    }
}
