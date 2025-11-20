package ru.nsu.zenin.file;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FileSubstringMatcherTest {

    @Test
    void test1() throws Exception {
        List<Integer> expected = new ArrayList<Integer>();
        expected.add(1);

        Path test =
                Paths.get(FileSubstringMatcherTest.class.getResource("/testfile/test1").toURI());

        Assertions.assertEquals(FileSubstringMatcher.find(test.toString(), "ab"), expected);
    }

    @BeforeAll
    static void initTestFiles() throws Exception {
        Path testsDir = Paths.get(FileSubstringMatcherTest.class.getResource("/testfile").toURI());

        Path test1 = testsDir.resolve("test1");
        Path test2 = testsDir.resolve("test2");
        Path test3 = testsDir.resolve("test3");

        writeFileIfNotExists(test1, "cab");
        writeFileIfNotExists(test1, "abcdabcabcdabcdab");
        writeFileIfNotExists(test1, "osoabcaabcababc");
    }

    private static void writeFileIfNotExists(Path file, Consumer<Writer> cons) throws Exception {
        if (!Files.exists(file)) {
            try (Writer writer = Files.newBufferedWriter(file)) {
                cons.accept(writer);
            }
        }
    }

    private static void writeFileIfNotExists(Path file, String str) throws Exception {
        if (!Files.exists(file)) {
            try (Writer writer = Files.newBufferedWriter(file)) {
                writer.write(str);
            }
        }
    }
}
