package ru.nsu.zenin.tester.service;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitService {

    public static Path cloneRepo(URL repoUrl) throws IOException, InterruptedException {
        Path cloneDir = Files.createTempDirectory("repo");

        ProcessBuilder pb = new ProcessBuilder("git", "clone", "--depth", "1", repoUrl.toString(), cloneDir.toString());
        pb.inheritIO();
        
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("Git clone failed with exit code: " + exitCode);
        }

        return cloneDir;
    }

    private static void deleteRecursively(Path dir) throws IOException {
        Files.walk(dir)
             .sorted((a, b) -> b.compareTo(a))
             .forEach(p -> {
                 try { Files.delete(p); } 
                 catch (IOException e) { System.err.println("Failed to delete: " + p); }
             });
    }
}
