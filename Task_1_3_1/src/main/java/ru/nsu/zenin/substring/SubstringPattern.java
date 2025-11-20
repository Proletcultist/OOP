package ru.nsu.zenin.substring;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class SubstringPattern {

    private final List<Integer> prefixFunction;
    private final String str;

    private SubstringPattern(List<Integer> prefixFunction, String str) {
        this.prefixFunction = prefixFunction;
        this.str = str;
    }

    public static SubstringPattern compile(String patt) {
        List<Integer> prefixFunction = new ArrayList<Integer>(patt.length());

        // Prefix function for 0 is always 0
        prefixFunction.add(0);

        for (int i = 1; i < patt.length(); i++) {
            // Get prefix function for previous position
            int k = prefixFunction.get(i - 1);

            // Try current char as continuation of previous prefix-suffix
            // Or try get next smaller prefix-suffix and try again
            while (k > 0 && patt.charAt(i) != patt.charAt(k)) {
                k = prefixFunction.get(k - 1);
            }

            // If prefix-suffix, to which we can add current char is found, this char prefix suffix
            // value is k + 1
            if (patt.charAt(i) == patt.charAt(k)) {
                k++;
            }

            prefixFunction.add(k);
        }

        return new SubstringPattern(prefixFunction, patt);
    }

    public SubstringMatcher matcher(Reader reader) {
        return new SubstringMatcher(this, reader);
    }

    int getPrefixFunctionValue(int index) {
        return prefixFunction.get(index);
    }

    String getString() {
        return str;
    }
}
