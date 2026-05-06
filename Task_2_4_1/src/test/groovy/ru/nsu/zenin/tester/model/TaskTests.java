package ru.nsu.zenin.tester.model;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTests {
    @Test
    void test() {
        LocalDate now = LocalDate.now();
        LocalDate notNow = now.plusDays(1);
        Task task = new Task("id", "name", 100, now, notNow);

        Assertions.assertEquals(task.id(), "id");
        Assertions.assertEquals(task.name(), "name");
        Assertions.assertEquals(task.maxScore(), 100);
        Assertions.assertEquals(task.softDeadline(), now);
        Assertions.assertEquals(task.hardDeadline(), notNow);
    }
}
