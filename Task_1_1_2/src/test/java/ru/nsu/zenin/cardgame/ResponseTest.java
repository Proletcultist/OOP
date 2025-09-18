package ru.nsu.zenin.cardgame;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResponseTest {

    @Test
    void responseTest() {
        Response res = new Response("ABOBA");

        Assertions.assertEquals(res.getText(), "ABOBA");
    }
}
