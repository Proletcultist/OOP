package ru.nsu.zenin.tester.service;

import ru.nsu.zenin.tester.model.Assignment;
import ru.nsu.zenin.tester.model.Checkpoint;
import ru.nsu.zenin.tester.model.Course;
import ru.nsu.zenin.tester.model.Student;

public class EvaluationService {
    private EvaluationService() {}

    public static void evaluateStudent(Course course, Student s) {
        for (Checkpoint c : course.getCheckpoints().values()) {
            s.getCheckpointScores().put(c, 0.0);
        }
        for (Assignment ass : s.getAssignments()) {
            double score = evaluateAssignment(ass);

            ass.setScore(score);

            for (Checkpoint check :
                    course.getCheckpoints()
                            .subMap(
                                    ass.getTask().hardDeadline(),
                                    false,
                                    course.getCheckpoints().lastKey(),
                                    true)
                            .values()) {
                s.getCheckpointScores().put(check, s.getCheckpointScores().get(check) + score);
            }
        }
    }

    private static double evaluateAssignment(Assignment ass) {
        if (!ass.isBuildable() || !ass.isCodestyleCompliant() || !ass.isHasDocs()) {
            return 0;
        }

        double deadlineFactor = 0.0;

        if (ass.getLastCommitDate().isBefore(ass.getTask().softDeadline())) {
            deadlineFactor += 0.5;
        }
        if (ass.getLastCommitDate().isBefore(ass.getTask().hardDeadline())) {
            deadlineFactor += 0.5;
        }

        return ass.getTask().maxScore() * deadlineFactor;
    }
}
