package ru.nsu.zenin.file;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import ru.nsu.zenin.substring.SubstringMatcher;
import ru.nsu.zenin.substring.SubstringPattern;

public class FileSubstringMatcher {
    private static final int BUFFER_SIZE = 8192;

    private FileSubstringMatcher() {}

    public static List<Integer> find(String file, String pattern, Charset charset)
            throws IOException {
        Path path = Paths.get(file);

        try (SubstringMatcher matcher =
                SubstringPattern.compile(pattern)
                        .matcher(
                                new InputStreamReader(
                                        new BufferedInputStream(
                                                Files.newInputStream(path), BUFFER_SIZE),
                                        charset))) {
            return matcher.matchAll();
        }
    }

    public static List<Integer> find(String file, String pattern) throws IOException {
        return find(file, pattern, StandardCharsets.UTF_8);
    }
}
