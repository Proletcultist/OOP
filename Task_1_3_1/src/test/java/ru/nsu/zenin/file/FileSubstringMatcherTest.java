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

    static final int BIG_TEST_SIZE = 100000;

    @Test
    void test1() throws Exception {
        List<Integer> expected = new ArrayList<Integer>();
        expected.add(1);

        Path test =
                Paths.get(FileSubstringMatcherTest.class.getResource("/testfile/test1").toURI());

        Assertions.assertEquals(FileSubstringMatcher.find(test.toString(), "ab"), expected);
    }

    @Test
    void test2() throws Exception {
        List<Integer> expected = new ArrayList<Integer>();
        expected.add(7);

        Path test =
                Paths.get(FileSubstringMatcherTest.class.getResource("/testfile/test2").toURI());

        Assertions.assertEquals(
                FileSubstringMatcher.find(test.toString(), "abcdabcabcdabcdab"), expected);
    }

    @Test
    void test3() throws Exception {
        List<Integer> expected = new ArrayList<Integer>();
        expected.add(3);
        expected.add(7);
        expected.add(12);

        Path test =
                Paths.get(FileSubstringMatcherTest.class.getResource("/testfile/test3").toURI());

        Assertions.assertEquals(FileSubstringMatcher.find(test.toString(), "abc"), expected);
    }

    @Test
    void test4() throws Exception {
        List<Integer> expected = new ArrayList<Integer>();
        expected.add(2);
        expected.add(4);
        expected.add(18);

        Path test =
                Paths.get(FileSubstringMatcherTest.class.getResource("/testfile/test4").toURI());

        Assertions.assertEquals(FileSubstringMatcher.find(test.toString(), "🤣😂"), expected);
    }

    @Test
    void test5() throws Exception {
        List<Integer> expected = new ArrayList<Integer>();
        expected.add(7);
        expected.add(16);

        Path test =
                Paths.get(FileSubstringMatcherTest.class.getResource("/testfile/test5").toURI());

        Assertions.assertEquals(FileSubstringMatcher.find(test.toString(), "ッシヅ"), expected);
    }

    @Test
    void testBig() throws Exception {
        Path test =
                Paths.get(FileSubstringMatcherTest.class.getResource("/testfile/testBig").toURI());

        List<Integer> result = FileSubstringMatcher.find(test.toString(), "abc");

        for (int i = 0; i < BIG_TEST_SIZE; i++) {
            Assertions.assertEquals(result.get(i), 1 + 12 * i);
        }
    }

    @BeforeAll
    static void initTestFiles() throws Exception {
        Path testsDir =
                Paths.get(FileSubstringMatcherTest.class.getResource("/").toURI())
                        .resolve("testfile");

        if (!Files.exists(testsDir)) {
            Files.createDirectory(testsDir);
        }

        Path test1 = testsDir.resolve("test1");
        Path test2 = testsDir.resolve("test2");
        Path test3 = testsDir.resolve("test3");
        Path test4 = testsDir.resolve("test4");
        Path test5 = testsDir.resolve("test5");
        Path testBig = testsDir.resolve("testBig");

        writeFileIfNotExists(test1, "cab");
        writeFileIfNotExists(test2, "osoabcdabcdabcabcdabcdab");
        writeFileIfNotExists(test3, "osoabcaabcababc");
        writeFileIfNotExists(test4, "🤣😀🤣😂🤣😂ABOBA!!11!1😂🤣😂");
        writeFileIfNotExists(test5, "ッシッシ🤣😀🤣ッシヅ😂🤣😂😂ッ🤣ッシヅ😂");

        writeFileIfNotExists(
                testBig,
                (writer) -> {
                    // abc occurs every 1 + 12n chars
                    for (int i = 0; i < BIG_TEST_SIZE; i++) {
                        try {
                            writer.write("aabcabシba🤣ab");
                        } catch (Exception e) {
                            throw new RuntimeException();
                        }
                    }
                });
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
