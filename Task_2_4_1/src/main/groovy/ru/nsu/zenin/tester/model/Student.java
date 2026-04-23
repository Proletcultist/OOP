package ru.nsu.zenin.tester.model;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import lombok.Data;
import ru.nsu.zenin.tester.service.GitService;
import ru.nsu.zenin.tester.service.GradleService;
import ru.nsu.zenin.tester.service.logging.Logger;

@Data
public class Student {
    private final String id;
    private final String fullName;
    private final URL ghRepo;

    private List<Assignment> assignments = new ArrayList<Assignment>();

    public void assign(Task task) {
        assignments.add(new Assignment(task));
    }

    public void checkAllAssignments() throws Exception {
        Logger.tryLog(Logger.LogLevel.INFO, "Checing assignments of " + id + " (" + fullName + ")...");

        Path repo = GitService.cloneRepo(ghRepo);

        for (Assignment ass : assignments) {
            Logger.tryLog(Logger.LogLevel.INFO, "Checking " + id + " : " + ass.getTask().id());

            ass.clearStatus();
            Path taskDir = repo.resolve(ass.getTask().id());

            if (!Files.exists(taskDir)) {
                Logger.tryLog(Logger.LogLevel.INFO, id + " : " + ass.getTask().id() + " doesn't exist");
                continue;
            }

            ass.setLastCommitDate(GitService.getLastCommitDate(taskDir));

            Logger.tryLog(Logger.LogLevel.INFO,"Building " + id + " : " + ass.getTask().id() + "...");
            try {
                GradleService.build(taskDir);
                ass.setBuildable(true);
            }
            catch (Exception e) {
                Logger.tryLog(Logger.LogLevel.INFO, id + " : " + ass.getTask().id() + " isn't buildable");
                continue;
            }

            Logger.tryLog(Logger.LogLevel.INFO,"Generating docs for " + id + " : " + ass.getTask().id() + "...");
            try {
                GradleService.generateJavadoc(taskDir);
                ass.setHasDocs(true);
            }
            catch (Exception e) {
                Logger.tryLog(Logger.LogLevel.INFO, "Cannot generate docs for " + id + " : " + ass.getTask().id());
            }

            Logger.tryLog(Logger.LogLevel.INFO,"Checking codestyle of " + id + " : " + ass.getTask().id() + "...");
            try {
                GradleService.checkStyle(taskDir);
                ass.setCodestyleCompliant(true);
            }
            catch (Exception e) {
                Logger.tryLog(Logger.LogLevel.INFO, "Style check failed for " + id + " : " + ass.getTask().id());
            }

            Logger.tryLog(Logger.LogLevel.INFO,"Running tests for " + id + " : " + ass.getTask().id() + "...");
            try {
                GradleService.TestReport rep = GradleService.runTestsAndReport(taskDir);
                ass.setTestsPassed(rep.passed());
                ass.setTestsFailed(rep.failed());
                ass.setTestsSkipped(rep.skipped());
            }
            catch (Exception e) {
                Logger.tryLog(Logger.LogLevel.INFO, "Tests for " + id + " : " + ass.getTask().id() + " failed");
            }
        }

        Logger.tryLog(Logger.LogLevel.INFO, "Removing " + ghRepo.toString() + "...");
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
