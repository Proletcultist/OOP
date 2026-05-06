package ru.nsu.zenin.tester.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.tester.model.Assignment;
import ru.nsu.zenin.tester.model.Checkpoint;
import ru.nsu.zenin.tester.model.Course;
import ru.nsu.zenin.tester.model.Group;
import ru.nsu.zenin.tester.model.Student;
import ru.nsu.zenin.tester.model.Task;

class ReportServiceTest {

    @Test
    void testFullHtmlReportFromResource() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        Course course = new Course();
        TreeMap<Double, String> scale = new TreeMap<Double, String>();
        scale.put(0.0, "F");
        scale.put(10.0, "A");
        course.setGradeScale(scale);

        LocalDate testDate = LocalDate.of(2026, 5, 6);
        Checkpoint cp = new Checkpoint("CP1", testDate);
        course.addCheckpoint(cp);

        Task task = new Task("lab1", "Lab 1", 10, testDate, testDate);
        Student student =
                new Student("s1", "Alice Smith", new URL("https://github.com/alice/repo"));
        student.assign(task);

        Assignment ass = student.getAssignments().get(0);
        ass.setBuildable(true);
        ass.setCodestyleCompliant(false);
        ass.setHasDocs(true);
        ass.setTestsPassed(5);
        ass.setTestsFailed(2);
        ass.setScore(10.0);

        student.getCheckpointScores().put(cp, 10.0);
        course.addGroup(new Group("Group 1", Collections.singletonList(student)));

        String expectedHtml;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("index.html")) {
            expectedHtml =
                    new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"))
                            + "\n";
        }

        String absPath =
                Paths.get(".", CheckService.DOCS_DIR, "s1", "lab1", "index.html")
                        .toAbsolutePath()
                        .toString();

        expectedHtml = expectedHtml.replace("${ABS_PATH}", absPath);

        try {
            ReportService.reportAllAssignments(Paths.get(".").toAbsolutePath(), course);

            String actual = outContent.toString().replace("\r\n", "\n");
            String expected = expectedHtml.replace("\r\n", "\n");

            Assertions.assertEquals(expected, actual);
        } finally {
            System.setOut(originalOut);
        }
    }
}
