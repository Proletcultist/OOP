package ru.nsu.zenin.substring;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubstringMatcherTest {

    @Test
    void test() throws IOException {
        SubstringPattern patt = SubstringPattern.compile("ab");
        try (SubstringMatcher matcher = patt.matcher(new StringReader("cab"))) {
            Assertions.assertEquals(matcher.nextMatch(), Optional.of(1));
        }
    }

    @Test
    void test2() throws IOException {
        SubstringPattern patt = SubstringPattern.compile("abcdabcabcdabcdab");
        try (SubstringMatcher matcher =
                patt.matcher(new StringReader("osoabcdabcdabcabcdabcdab"))) {
            Assertions.assertEquals(matcher.nextMatch(), Optional.of(7));
            Assertions.assertEquals(matcher.nextMatch(), Optional.empty());
        }
    }

    @Test
    void test3() throws IOException {
        SubstringPattern patt = SubstringPattern.compile("abc");
        List<Integer> matches = new ArrayList<Integer>();
        matches.add(3);
        matches.add(7);
        matches.add(12);

        try (SubstringMatcher matcher = patt.matcher(new StringReader("osoabcaabcababc"))) {
            Assertions.assertEquals(matcher.matchAll(), matches);
        }
    }

    @Test
    void test4() throws IOException {
        SubstringPattern patt = SubstringPattern.compile("🤣😂");
        try (SubstringMatcher matcher =
                patt.matcher(new StringReader("🤣😀🤣😂🤣😂ABOBA!!11!1😂🤣😂"))) {
            Assertions.assertEquals(matcher.nextMatch(), Optional.of(2));
            Assertions.assertEquals(matcher.nextMatch(), Optional.of(4));
            Assertions.assertEquals(matcher.nextMatch(), Optional.of(18));
            Assertions.assertEquals(matcher.nextMatch(), Optional.empty());
        }
    }

    @Test
    void test5() throws IOException {
        SubstringPattern patt = SubstringPattern.compile("ッシヅ");
        try (SubstringMatcher matcher =
                patt.matcher(new StringReader("ッシッシ🤣😀🤣ッシヅ😂🤣😂😂ッ🤣ッシヅ😂"))) {
            Assertions.assertEquals(matcher.nextMatch(), Optional.of(7));
            Assertions.assertEquals(matcher.nextMatch(), Optional.of(16));
            Assertions.assertEquals(matcher.nextMatch(), Optional.empty());
        }
    }
}
