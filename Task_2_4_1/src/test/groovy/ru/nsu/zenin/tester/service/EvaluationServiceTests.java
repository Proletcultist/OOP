package ru.nsu.zenin.tester.service;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.tester.model.*;

class EvaluationServiceTests {
    private Course course;
    private Student student;
    private Task task;

    @BeforeEach
    void setUp() throws Exception {
        course = new Course();
        task = new Task("id", "name", 10, LocalDate.now().plusDays(2), LocalDate.now().plusDays(5));
        course.addTask(task);

        student = new Student("sid", "sname", new java.net.URL("http://example.com/"));
        student.assign(task);

        Checkpoint cp = new Checkpoint("ch", LocalDate.now().plusDays(10));
        course.addCheckpoint(cp);
    }

    @Test
    void testEvaluateAssignment_FullScore() {
        Assignment ass = student.getAssignments().get(0);
        ass.setBuildable(true);
        ass.setCodestyleCompliant(true);
        ass.setHasDocs(true);
        ass.setLastCommitDate(LocalDate.now());

        EvaluationService.evaluateStudent(course, student);

        Assertions.assertEquals(10.0, ass.getScore());
        Assertions.assertEquals(
                10.0,
                student.getCheckpointScores()
                        .get(course.getCheckpoints().get(course.getCheckpoints().lastKey())));
    }

    @Test
    void testEvaluateAssignment_HalfScore() {
        Assignment ass = student.getAssignments().get(0);
        ass.setBuildable(true);
        ass.setCodestyleCompliant(true);
        ass.setHasDocs(true);
        ass.setLastCommitDate(LocalDate.now().plusDays(3));

        EvaluationService.evaluateStudent(course, student);

        Assertions.assertEquals(5.0, ass.getScore());
    }

    @Test
    void testEvaluateAssignment_ZeroScore_RequirementsFailed() {
        Assignment ass = student.getAssignments().get(0);
        ass.setBuildable(false);
        ass.setCodestyleCompliant(true);
        ass.setHasDocs(true);
        ass.setLastCommitDate(LocalDate.now());

        EvaluationService.evaluateStudent(course, student);

        Assertions.assertEquals(0.0, ass.getScore());
    }
}
