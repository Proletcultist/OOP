package ru.nsu.zenin.tester.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import ru.nsu.zenin.tester.service.logging.Logger;

public class GitService {
    private static DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss xx");

    private GitService() {}

    public static Path cloneRepo(URL repoUrl) throws Exception {
        Path cloneDir = Files.createTempDirectory("repo");

        Logger.tryLog(Logger.LogLevel.INFO, "Cloning " + repoUrl.toString() + "...");
        ProcessBuilder pb =
                new ProcessBuilder(
                        "git", "clone", "--single-branch", repoUrl.toString(), cloneDir.toString());
        pb.inheritIO();

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Git clone failed with exit code: " + exitCode);
        }

        Logger.tryLog(Logger.LogLevel.INFO, "Cloned " + repoUrl.toString() + " successfully");

        return cloneDir;
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

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Git log failed with exit code: " + exitCode);
        }

        return LocalDate.parse(timestampStr, dateFormatter);
    }
}
