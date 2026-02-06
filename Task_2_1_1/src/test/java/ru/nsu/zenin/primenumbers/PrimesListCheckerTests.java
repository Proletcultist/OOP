package ru.nsu.zenin.primenumbers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class PrimesListCheckerTests {

    private static List<Integer> bigList;

    @BeforeAll
    static void initBigList() {
        bigList = new ArrayList<Integer>();

        for (int i = 0; i < 1000000; i++) {
            if (isPrime(i)) {
                bigList.add(i);
            }
        }
        bigList.add(4);
    }

    @Test
    void testFalse() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);

        Assertions.assertFalse(PrimesListChecker.isAnyCompoundInListSequentially(list));
    }

    @Test
    void testTrue() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(666);
        list.add(3);

        Assertions.assertTrue(PrimesListChecker.isAnyCompoundInListSequentially(list));
    }

    @Test
    void testTrueSquared() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(256);

        Assertions.assertTrue(PrimesListChecker.isAnyCompoundInListSequentially(list));
    }

    @Test
    void testFalseMT() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);

        Assertions.assertFalse(PrimesListChecker.isAnyCompoundInListMT(list));
    }

    @Test
    void testTrueMT() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(666);
        list.add(3);

        Assertions.assertTrue(PrimesListChecker.isAnyCompoundInListMT(list));
    }

    @Test
    void testTrueSquaredMT() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(256);

        Assertions.assertTrue(PrimesListChecker.isAnyCompoundInListMT(list));
    }

    @Test
    void testFalsePS() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);

        Assertions.assertFalse(PrimesListChecker.isAnyCompoundInListPS(list));
    }

    @Test
    void testTruePS() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(666);
        list.add(3);

        Assertions.assertTrue(PrimesListChecker.isAnyCompoundInListPS(list));
    }

    @Test
    void testTrueSquaredPS() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(256);

        Assertions.assertTrue(PrimesListChecker.isAnyCompoundInListPS(list));
    }

    @TestFactory
    Collection<DynamicTest> bigTestsMT() {
        ArrayList<DynamicTest> tests = new ArrayList<DynamicTest>();
        for (int i = 1; i < Runtime.getRuntime().availableProcessors(); i++) {
            int testI = i;
            tests.add(
                    DynamicTest.dynamicTest(
                            "MT Test with " + Integer.toString(i) + " threads",
                            () -> {
                                Assertions.assertTrue(
                                        PrimesListChecker.isAnyCompoundInListMT(bigList, testI));
                            }));
        }
        return tests;
    }

    private static boolean isPrime(int n) {
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
