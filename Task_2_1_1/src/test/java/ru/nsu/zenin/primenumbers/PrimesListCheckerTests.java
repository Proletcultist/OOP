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

        PrimesListChecker checker = new PrimesListCheckerSequential();
        Assertions.assertFalse(checker.isAnyCompoundInList(list));
    }

    @Test
    void testTrue() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(666);
        list.add(3);

        PrimesListChecker checker = new PrimesListCheckerSequential();
        Assertions.assertTrue(checker.isAnyCompoundInList(list));
    }

    @Test
    void testTrueSquared() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(256);

        PrimesListChecker checker = new PrimesListCheckerSequential();
        Assertions.assertTrue(checker.isAnyCompoundInList(list));
    }

    @Test
    void testFalseMT() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);

        PrimesListChecker checker = new PrimesListCheckerMT();
        Assertions.assertFalse(checker.isAnyCompoundInList(list));
    }

    @Test
    void testTrueMT() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(666);
        list.add(3);

        PrimesListChecker checker = new PrimesListCheckerMT();
        Assertions.assertTrue(checker.isAnyCompoundInList(list));
    }

    @Test
    void testTrueSquaredMT() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(256);

        PrimesListChecker checker = new PrimesListCheckerMT();
        Assertions.assertTrue(checker.isAnyCompoundInList(list));
    }

    @Test
    void testFalsePS() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);

        PrimesListChecker checker = new PrimesListCheckerPS();
        Assertions.assertFalse(checker.isAnyCompoundInList(list));
    }

    @Test
    void testTruePS() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(666);
        list.add(3);

        PrimesListChecker checker = new PrimesListCheckerPS();
        Assertions.assertTrue(checker.isAnyCompoundInList(list));
    }

    @Test
    void testTrueSquaredPS() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(256);

        PrimesListChecker checker = new PrimesListCheckerPS();
        Assertions.assertTrue(checker.isAnyCompoundInList(list));
    }

    @Test
    void bigTestSeq() {
        PrimesListChecker checker = new PrimesListCheckerSequential();
        Assertions.assertTrue(checker.isAnyCompoundInList(bigList));
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
                                PrimesListCheckerMT checker = new PrimesListCheckerMT();
                                Assertions.assertTrue(checker.isAnyCompoundInList(bigList, testI));
                            }));
        }
        return tests;
    }

    @Test
    void bigTestPS() {
        PrimesListChecker checker = new PrimesListCheckerPS();
        Assertions.assertTrue(checker.isAnyCompoundInList(bigList));
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
