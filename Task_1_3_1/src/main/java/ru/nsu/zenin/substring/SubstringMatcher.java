package ru.nsu.zenin.substring;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubstringMatcher implements AutoCloseable {
    private final SubstringPattern pattern;
    private final Reader reader;

    private int index = -1;
    private int k = 0;
    private int surrogatePairsPassed = 0;

    SubstringMatcher(SubstringPattern pattern, Reader reader) {
        this.pattern = pattern;
        this.reader = reader;
    }

    public List<Integer> matchAll() throws IOException {
        List<Integer> out = new ArrayList<Integer>();

        Optional<Integer> next = nextMatch();
        while (next.isPresent()) {
            out.add(next.get());
            next = nextMatch();
        }

        return out;
    }

    public Optional<Integer> nextMatch() throws IOException {
        while (true) {
            int ch = reader.read();
            index++;

            // If no chars in reader left, return empty
            if (ch == -1) {
                break;
            }

            if (Character.isLowSurrogate((char) ch)) {
                surrogatePairsPassed++;
            }

            // Try current char as continuation of previous prefix-suffix
            // Or try get next smaller prefix-suffix and try again
            while (k > 0 && (char) ch != pattern.getString().charAt(k)) {
                k = pattern.getPrefixFunctionValue(k - 1);
            }

            // If prefix-suffix, to which we can add current char is found, this char prefix suffix
            // value is k + 1
            if ((char) ch == pattern.getString().charAt(k)) {
                k++;
            }

            if (k == pattern.getString().length()) {
                k = pattern.getPrefixFunctionValue(k - 1);
                return Optional.of(
                        (index + 1 - pattern.getString().length())
                                - (surrogatePairsPassed - pattern.getSurrogatePairsAmount()));
            }
        }

        return Optional.empty();
    }

    public void close() throws IOException {
        reader.close();
    }
}
