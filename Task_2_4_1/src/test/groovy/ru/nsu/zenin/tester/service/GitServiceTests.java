package ru.nsu.zenin.tester.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GitServiceTests {

    @Test
    void testGetLastCommitDate(@TempDir Path tempDir) throws Exception {
        runCommand(tempDir, "git", "init");
        runCommand(tempDir, "git", "config", "user.email", "test@example.com");
        runCommand(tempDir, "git", "config", "user.name", "Tester");

        Path dummyFile = tempDir.resolve("README.md");
        Files.writeString(dummyFile, "# Test Repo");

        runCommand(tempDir, "git", "add", ".");
        runCommand(tempDir, "git", "commit", "-m", "Initial commit");

        LocalDate date = GitService.getLastCommitDate(tempDir);

        Assertions.assertNotNull(date);
        Assertions.assertEquals(LocalDate.now(), date);
    }

    private void runCommand(Path dir, String... args) throws Exception {
        new ProcessBuilder(args).directory(dir.toFile()).start().waitFor();
    }
}
