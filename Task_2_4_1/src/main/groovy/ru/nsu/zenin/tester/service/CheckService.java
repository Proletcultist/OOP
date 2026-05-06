package ru.nsu.zenin.tester.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import ru.nsu.zenin.tester.model.Assignment;
import ru.nsu.zenin.tester.model.Course;
import ru.nsu.zenin.tester.model.Group;
import ru.nsu.zenin.tester.model.Student;
import ru.nsu.zenin.tester.service.logging.Logger;

public class CheckService {
    static final String DOCS_DIR = "docs";

    private CheckService() {}

    public static void checkAllAssignments(Course course) {
        List<StudentCheckTask> studentTasks = new ArrayList<StudentCheckTask>();

        for (Group g : course.getGroups()) {
            for (Student s : g.students()) {
                studentTasks.add(new StudentCheckTask(s));
            }
        }

        for (StudentCheckTask task : studentTasks) {
            task.fork();
        }
        for (StudentCheckTask task : studentTasks) {
            task.join();
        }
    }

    private static class StudentCheckTask extends RecursiveAction {
        private final Student student;

        StudentCheckTask(Student student) {
            this.student = student;
        }

        @Override
        protected void compute() {
            Logger.tryLog(
                    Logger.LogLevel.INFO,
                    "Checking assignments of "
                            + student.getId()
                            + " ("
                            + student.getFullName()
                            + ")...");
            Path repo;
            try {
                repo = GitService.cloneRepo(student.getGhRepo());
            } catch (Exception e) {
                Logger.tryLog(
                        Logger.LogLevel.WARNING,
                        "Failed to clone repo of student "
                                + student.getId()
                                + ": "
                                + e.getMessage());
                return;
            }

            List<AssignmentCheckTask> assignmentTasks = new ArrayList<AssignmentCheckTask>();
            for (Assignment ass : student.getAssignments()) {
                assignmentTasks.add(new AssignmentCheckTask(student, ass, repo));
            }

            invokeAll(assignmentTasks);

            Logger.tryLog(
                    Logger.LogLevel.INFO, "Removing " + student.getGhRepo().toString() + "...");

            try {
                Files.walk(repo)
                        .sorted((a, b) -> b.compareTo(a))
                        .forEach(
                                p -> {
                                    try {
                                        Files.delete(p);
                                    } catch (IOException e) {
                                        Logger.tryLog(
                                                Logger.LogLevel.WARNING, "Failed to delete: " + p);
                                    }
                                });
            } catch (IOException e) {
                Logger.tryLog(Logger.LogLevel.WARNING, "Failed to delete repo " + repo);
            }
        }
    }

    private static class AssignmentCheckTask extends RecursiveAction {
        private final Student student;
        private final Assignment assignment;
        private final Path repo;

        AssignmentCheckTask(Student student, Assignment assignment, Path repo) {
            this.student = student;
            this.assignment = assignment;
            this.repo = repo;
        }

        @Override
        protected void compute() {
            Logger.tryLog(
                    Logger.LogLevel.INFO,
                    "Checking " + student.getId() + " : " + assignment.getTask().id());

            assignment.clearStatus();

            try {
                Path taskDir = repo.resolve(assignment.getTask().id());

                if (!Files.exists(taskDir)) {
                    Logger.tryLog(
                            Logger.LogLevel.INFO,
                            student.getId() + " : " + assignment.getTask().id() + " doesn't exist");
                    return;
                }

                assignment.setLastCommitDate(GitService.getLastCommitDate(taskDir));

                Logger.tryLog(
                        Logger.LogLevel.INFO,
                        "Building " + student.getId() + " : " + assignment.getTask().id() + "...");
                try {
                    GradleService.build(taskDir);
                    assignment.setBuildable(true);
                } catch (Exception e) {
                    Logger.tryLog(
                            Logger.LogLevel.INFO,
                            student.getId()
                                    + " : "
                                    + assignment.getTask().id()
                                    + " isn't buildable");
                    return;
                }

                Logger.tryLog(
                        Logger.LogLevel.INFO,
                        "Generating docs for "
                                + student.getId()
                                + " : "
                                + assignment.getTask().id()
                                + "...");
                try {
                    GradleService.generateJavadoc(
                            taskDir,
                            Paths.get(DOCS_DIR, student.getId(), assignment.getTask().id())
                                    .toAbsolutePath());
                    assignment.setHasDocs(true);
                } catch (Exception e) {
                    Logger.tryLog(
                            Logger.LogLevel.INFO,
                            "Cannot generate docs for "
                                    + student.getId()
                                    + " : "
                                    + assignment.getTask().id());
                }

                Logger.tryLog(
                        Logger.LogLevel.INFO,
                        "Checking codestyle of "
                                + student.getId()
                                + " : "
                                + assignment.getTask().id()
                                + "...");
                try {
                    GradleService.checkStyle(taskDir);
                    assignment.setCodestyleCompliant(true);
                } catch (Exception e) {
                    Logger.tryLog(
                            Logger.LogLevel.INFO,
                            "Style check failed for "
                                    + student.getId()
                                    + " : "
                                    + assignment.getTask().id());
                }

                Logger.tryLog(
                        Logger.LogLevel.INFO,
                        "Running tests for "
                                + student.getId()
                                + " : "
                                + assignment.getTask().id()
                                + "...");
                try {
                    GradleService.TestReport rep = GradleService.runTestsAndReport(taskDir);
                    assignment.setTestsPassed(rep.passed());
                    assignment.setTestsFailed(rep.failed());
                    assignment.setTestsSkipped(rep.skipped());
                } catch (Exception e) {
                    Logger.tryLog(
                            Logger.LogLevel.INFO,
                            "Tests for "
                                    + student.getId()
                                    + " : "
                                    + assignment.getTask().id()
                                    + " failed");
                }
            } catch (Exception e) {
                Logger.tryLog(
                        Logger.LogLevel.WARNING,
                        "Failed to check assignment "
                                + assignment.getTask().id()
                                + " for student "
                                + student.getId()
                                + ": "
                                + e.getMessage());
            }
        }
    }
}
