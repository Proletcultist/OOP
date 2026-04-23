package ru.nsu.zenin.tester.service;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import ru.nsu.zenin.tester.service.logging.Logger;

public class GradleService {
    private GradleService() {}

    public static void build(Path taskDir) throws Exception {
        runCommand(taskDir, "gradle", "assemble", "-q");
    }

    public static void generateJavadoc(Path taskDir) throws Exception {
        runCommand(taskDir, "gradle", "javadoc", "-q");
    }

    public static void checkStyle(Path taskDir) throws Exception {
        runCommand(taskDir, "gradle", "spotlessCheck", "-q");
    }

    public static TestReport runTestsAndReport(Path taskDir) throws Exception {
        runCommand(taskDir, "gradle", "test", "-q");
        return parseTestReports(taskDir.resolve("build/test-results/test"));
    }

    private static TestReport parseTestReports(Path reportDir) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        int passed = 0, failed = 0, skipped = 0;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(reportDir, "TEST-*.xml")) {
            for (Path xmlFile : stream) {
                try {
                    Element suite = builder.parse(xmlFile.toFile()).getDocumentElement();

                    int tests = Integer.parseInt(suite.getAttribute("tests").trim());
                    int failures = Integer.parseInt(suite.getAttribute("failures").trim());
                    int errs = Integer.parseInt(suite.getAttribute("errors").trim());
                    int skips = Integer.parseInt(suite.getAttribute("skipped").trim());

                    failed += failures;
                    skipped += skips;
                    passed += Math.max(0, tests - failures - errs - skips);
                } catch (Exception e) {
                    Logger.tryLog(
                            Logger.LogLevel.WARNING, "Skipping malformed test report: " + xmlFile);
                }
            }
        }

        return new TestReport(passed, failed, skipped);
    }

    private static void runCommand(Path taskDir, String... args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(taskDir.toFile());
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        pb.redirectError(ProcessBuilder.Redirect.DISCARD);
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Gradle failed with exit code: " + exitCode);
        }
    }

    public record TestReport(int passed, int failed, int skipped) {}
}
