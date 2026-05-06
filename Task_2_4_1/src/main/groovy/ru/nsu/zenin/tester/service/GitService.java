package ru.nsu.zenin.tester.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import ru.nsu.zenin.tester.service.logging.Logger;

public class GitService {
    private static DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss xx");

    private GitService() {}

    public static Path cloneRepo(URL repoUrl) throws Exception {
        Path cloneDir = Files.createTempDirectory("repo");

        try {
            Logger.tryLog(Logger.LogLevel.INFO, "Cloning " + repoUrl.toString() + "...");
            ProcessBuilder pb =
                    new ProcessBuilder(
                            "git",
                            "clone",
                            "--single-branch",
                            repoUrl.toString(),
                            cloneDir.toString());
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            pb.redirectError(ProcessBuilder.Redirect.DISCARD);

            Process process = pb.start();
            boolean inTime = process.waitFor(5, TimeUnit.MINUTES);
            if (!inTime) {
                throw new RuntimeException("Git timeout");
            } else if (process.exitValue() != 0) {
                throw new RuntimeException(
                        "Git clone failed with exit code: " + process.exitValue());
            }

            Logger.tryLog(Logger.LogLevel.INFO, "Cloned " + repoUrl.toString() + " successfully");

            return cloneDir;
        } catch (Exception e) {
            Files.walk(cloneDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(
                            p -> {
                                try {
                                    Files.delete(p);
                                } catch (IOException e2) {
                                    Logger.tryLog(
                                            Logger.LogLevel.WARNING, "Failed to delete: " + p);
                                }
                            });
            throw e;
        }
    }

    public static LocalDate getLastCommitDate(Path file) throws Exception {
        ProcessBuilder pb =
                new ProcessBuilder("git", "log", "-1", "--format=%ci", "--", file.toString());

        pb.directory(file.toFile());
        pb.redirectError(ProcessBuilder.Redirect.DISCARD);
        Process process = pb.start();

        String timestampStr;
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            timestampStr = reader.readLine();
            while (reader.readLine() != null) {}
        }

        boolean inTime = process.waitFor(5, TimeUnit.MINUTES);
        if (!inTime) {
            throw new RuntimeException("Git timeout");
        } else if (process.exitValue() != 0) {
            throw new RuntimeException("Git log failed with exit code: " + process.exitValue());
        }

        return LocalDate.parse(timestampStr, dateFormatter);
    }
}
