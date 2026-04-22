package ru.nsu.zenin.tester.service;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import ru.nsu.zenin.tester.service.logging.Logger;

public class GitService {
    private GitService() {}

    public static Path cloneRepo(URL repoUrl) throws IOException, InterruptedException {
        Path cloneDir = Files.createTempDirectory("repo");

        Logger.tryLog(Logger.LogLevel.INFO, "Cloning " + repoUrl.toString() + "...");
        ProcessBuilder pb =
                new ProcessBuilder(
                        "git", "clone", "--depth", "1", repoUrl.toString(), cloneDir.toString());
        pb.inheritIO();

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("Git clone failed with exit code: " + exitCode);
        }

        Logger.tryLog(Logger.LogLevel.INFO, "Cloned " + repoUrl.toString() + " successfully");

        return cloneDir;
    }
}
