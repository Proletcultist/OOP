package ru.nsu.zenin.tester.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ru.nsu.zenin.tester.model.Assignment;
import ru.nsu.zenin.tester.model.Course;
import ru.nsu.zenin.tester.model.Group;
import ru.nsu.zenin.tester.model.Student;
import ru.nsu.zenin.tester.service.logging.Logger;

public class CheckService {
    static final String DOCS_DIR = "docs";

    private CheckService() {}

    public static void checkAllAssignments(Course course) throws Exception {
        for (Group g : course.getGroups()) {
            for (Student s : g.students()) {
                checkAllAssignments(s);
            }
        }
    }

    public static void checkAllAssignments(Student student) throws Exception {
        Logger.tryLog(
                Logger.LogLevel.INFO,
                "Checking assignments of "
                        + student.getId()
                        + " ("
                        + student.getFullName()
                        + ")...");

        Path repo = GitService.cloneRepo(student.getGhRepo());

        for (Assignment ass : student.getAssignments()) {
            Logger.tryLog(
                    Logger.LogLevel.INFO,
                    "Checking " + student.getId() + " : " + ass.getTask().id());

            ass.clearStatus();
            Path taskDir = repo.resolve(ass.getTask().id());

            if (!Files.exists(taskDir)) {
                Logger.tryLog(
                        Logger.LogLevel.INFO,
                        student.getId() + " : " + ass.getTask().id() + " doesn't exist");
                continue;
            }

            ass.setLastCommitDate(GitService.getLastCommitDate(taskDir));

            Logger.tryLog(
                    Logger.LogLevel.INFO,
                    "Building " + student.getId() + " : " + ass.getTask().id() + "...");
            try {
                GradleService.build(taskDir);
                ass.setBuildable(true);
            } catch (Exception e) {
                Logger.tryLog(
                        Logger.LogLevel.INFO,
                        student.getId() + " : " + ass.getTask().id() + " isn't buildable");
                continue;
            }

            Logger.tryLog(
                    Logger.LogLevel.INFO,
                    "Generating docs for " + student.getId() + " : " + ass.getTask().id() + "...");
            try {
                GradleService.generateJavadoc(
                        taskDir,
                        Paths.get(DOCS_DIR, student.getId(), ass.getTask().id()).toAbsolutePath());
                ass.setHasDocs(true);
            } catch (Exception e) {
                Logger.tryLog(
                        Logger.LogLevel.INFO,
                        "Cannot generate docs for " + student.getId() + " : " + ass.getTask().id());
            }

            Logger.tryLog(
                    Logger.LogLevel.INFO,
                    "Checking codestyle of "
                            + student.getId()
                            + " : "
                            + ass.getTask().id()
                            + "...");
            try {
                GradleService.checkStyle(taskDir);
                ass.setCodestyleCompliant(true);
            } catch (Exception e) {
                Logger.tryLog(
                        Logger.LogLevel.INFO,
                        "Style check failed for " + student.getId() + " : " + ass.getTask().id());
            }

            Logger.tryLog(
                    Logger.LogLevel.INFO,
                    "Running tests for " + student.getId() + " : " + ass.getTask().id() + "...");
            try {
                GradleService.TestReport rep = GradleService.runTestsAndReport(taskDir);
                ass.setTestsPassed(rep.passed());
                ass.setTestsFailed(rep.failed());
                ass.setTestsSkipped(rep.skipped());
            } catch (Exception e) {
                Logger.tryLog(
                        Logger.LogLevel.INFO,
                        "Tests for " + student.getId() + " : " + ass.getTask().id() + " failed");
            }
        }

        Logger.tryLog(Logger.LogLevel.INFO, "Removing " + student.getGhRepo().toString() + "...");

        Files.walk(repo)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(
                        p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                Logger.tryLog(Logger.LogLevel.WARNING, "Failed to delete: " + p);
                            }
                        });
    }
}
