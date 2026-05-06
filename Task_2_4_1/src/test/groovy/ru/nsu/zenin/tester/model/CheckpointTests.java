package ru.nsu.zenin.tester.model;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CheckpointTests {
    @Test
    void test() {
        LocalDate date = LocalDate.now();
        Checkpoint c = new Checkpoint("id", date);

        Assertions.assertEquals(c.id(), "id");
        Assertions.assertEquals(c.date(), date);
    }
}
