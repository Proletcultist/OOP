package ru.nsu.zenin.tester.service;

import java.nio.file.Path;
import ru.nsu.zenin.tester.service.logging.Logger;

public class GradleService {
    private GradleService() {}

    public static void build(Path taskDir) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("gradle", "assemble", "-q");
        pb.directory(taskDir.toFile());
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        pb.redirectError(ProcessBuilder.Redirect.DISCARD);
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new Exception("Gradle failed with exit code: " + exitCode);
        }
    }

}
