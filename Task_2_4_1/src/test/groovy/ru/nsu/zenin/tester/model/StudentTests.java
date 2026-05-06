package ru.nsu.zenin.tester.model;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StudentTests {
    @Test
    void test() throws Exception {
        Student s = new Student("id", "name", new URL("http://example.com/"));

        LocalDate now = LocalDate.now();
        LocalDate notNow = now.plusDays(1);
        Task task = new Task("id", "name", 100, now, notNow);

        s.assign(task);
        s.getCheckpointScores().put(new Checkpoint("id", notNow), 20.0);

        Map<Checkpoint, Double> checks = new HashMap<Checkpoint, Double>();
        checks.put(new Checkpoint("id", notNow), 20.0);

        Assertions.assertEquals(s.getId(), "id");
        Assertions.assertEquals(s.getFullName(), "name");
        Assertions.assertEquals(s.getGhRepo(), new URL("http://example.com/"));
        Assertions.assertEquals(s.getAssignments().size(), 1);
        Assertions.assertEquals(s.getAssignments().get(0).getTask(), task);
        Assertions.assertEquals(s.getCheckpointScores(), checks);
    }
}
