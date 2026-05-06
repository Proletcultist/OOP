package ru.nsu.zenin.tester.app;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppTest {
    @Test
    void test(@TempDir Path tempDir) throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        Path resDir =
                Paths.get(getClass().getClassLoader().getResource("config.groovy").toURI())
                        .getParent();
        Files.copy(resDir.resolve("config.groovy"), tempDir.resolve("config.groovy"));
        Files.copy(resDir.resolve("proletcultist.groovy"), tempDir.resolve("proletcultist.groovy"));
        Files.copy(resDir.resolve("chebupelka.groovy"), tempDir.resolve("chebupelka.groovy"));

        String expectedHtml;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("integ_index.html")) {
            expectedHtml =
                    new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"))
                            + "\n";
        }

        expectedHtml = expectedHtml.replace("${ROOT_DIR}", tempDir.toString());

        try {
            String[] args = {tempDir.toString()};
            App.main(args);

            String actual = outContent.toString().replace("\r\n", "\n");
            String expected = expectedHtml.replace("\r\n", "\n");

            Assertions.assertEquals(expected, actual);
        } finally {
            System.setOut(originalOut);
        }
    }
}
