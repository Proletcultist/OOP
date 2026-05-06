package ru.nsu.zenin.tester.model;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AssignmentTests {
    @Test
    void gettersSettersTest() {
        Task task = new Task("id", "name", 100, LocalDate.now(), LocalDate.now());
        LocalDate date = LocalDate.now();

        Assignment ass = new Assignment(task);

        ass.setLastCommitDate(date);
        ass.setBuildable(true);
        ass.setCodestyleCompliant(true);
        ass.setHasDocs(true);
        ass.setTestsPassed(1);
        ass.setTestsFailed(2);
        ass.setTestsSkipped(3);
        ass.setScore(100);

        Assertions.assertEquals(ass.getTask(), task);
        Assertions.assertEquals(ass.getLastCommitDate(), date);
        Assertions.assertEquals(ass.isBuildable(), true);
        Assertions.assertEquals(ass.isHasDocs(), true);
        Assertions.assertEquals(ass.isCodestyleCompliant(), true);
        Assertions.assertEquals(ass.getTestsPassed(), 1);
        Assertions.assertEquals(ass.getTestsFailed(), 2);
        Assertions.assertEquals(ass.getTestsSkipped(), 3);
        Assertions.assertEquals(ass.getScore(), 100);

        ass.clearStatus();

        Assertions.assertEquals(ass.isBuildable(), false);
        Assertions.assertEquals(ass.isHasDocs(), false);
        Assertions.assertEquals(ass.isCodestyleCompliant(), false);
        Assertions.assertEquals(ass.getTestsPassed(), 0);
        Assertions.assertEquals(ass.getTestsFailed(), 0);
        Assertions.assertEquals(ass.getTestsSkipped(), 0);
        Assertions.assertEquals(ass.getScore(), 0);
    }
}
