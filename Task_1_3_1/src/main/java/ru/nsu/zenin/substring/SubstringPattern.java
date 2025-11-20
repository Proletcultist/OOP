package ru.nsu.zenin.substring;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class SubstringPattern {

    private final List<Integer> prefixFunction;
    private final String str;
    private int surrogatePairsAmount = 0;

    private SubstringPattern(List<Integer> prefixFunction, String str, int surrogatePairsAmount) {
        this.prefixFunction = prefixFunction;
        this.str = str;
        this.surrogatePairsAmount = surrogatePairsAmount;
    }

    public static SubstringPattern compile(String patt) {
        List<Integer> prefixFunction = new ArrayList<Integer>(patt.length());

        // Prefix function for 0 is always 0
        prefixFunction.add(0);

        int surrogatePairsAmount = 0;

        for (int i = 1; i < patt.length(); i++) {
            // Get prefix function for previous position
            int k = prefixFunction.get(i - 1);
            char currChar = patt.charAt(i);

            // Try current char as continuation of previous prefix-suffix
            // Or try get next smaller prefix-suffix and try again
            while (k > 0 && currChar != patt.charAt(k)) {
                k = prefixFunction.get(k - 1);
            }

            // If prefix-suffix, to which we can add current char is found, this char prefix suffix
            // value is k + 1
            if (currChar == patt.charAt(k)) {
                k++;
            }

            prefixFunction.add(k);

            if (Character.isLowSurrogate(currChar)) {
                surrogatePairsAmount++;
            }
        }

        return new SubstringPattern(prefixFunction, patt, surrogatePairsAmount);
    }

    public SubstringMatcher matcher(Reader reader) {
        return new SubstringMatcher(this, reader);
    }

    int getPrefixFunctionValue(int index) {
        return prefixFunction.get(index);
    }

    int getSurrogatePairsAmount() {
        return surrogatePairsAmount;
    }

    String getString() {
        return str;
    }
}
