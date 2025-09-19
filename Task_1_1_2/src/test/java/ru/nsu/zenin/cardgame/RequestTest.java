package ru.nsu.zenin.cardgame;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RequestTest {

    @Test
    void requestTest() {
        Request req = new Request("ABOBA");

        Assertions.assertEquals(req.getText(), "ABOBA");
    }
}
