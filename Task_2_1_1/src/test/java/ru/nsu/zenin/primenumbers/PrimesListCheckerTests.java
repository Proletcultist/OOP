package ru.nsu.zenin.primenumbers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest
    @ValueSource(
            classes = {
                PrimesListCheckerSequential.class,
                PrimesListCheckerMT.class,
                PrimesListCheckerPS.class
            })
    <T extends PrimesListChecker> void testFalse(Class<T> checkerClass) throws Exception {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);

        PrimesListChecker checker = checkerClass.newInstance();
        Assertions.assertFalse(checker.isAnyCompoundInList(list));
    }

    @ParameterizedTest
    @ValueSource(
            classes = {
                PrimesListCheckerSequential.class,
                PrimesListCheckerMT.class,
                PrimesListCheckerPS.class
            })
    <T extends PrimesListChecker> void testTrue(Class<T> checkerClass) throws Exception {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(666);
        list.add(3);

        PrimesListChecker checker = checkerClass.newInstance();
        Assertions.assertTrue(checker.isAnyCompoundInList(list));
    }

    @ParameterizedTest
    @ValueSource(
            classes = {
                PrimesListCheckerSequential.class,
                PrimesListCheckerMT.class,
                PrimesListCheckerPS.class
            })
    <T extends PrimesListChecker> void testTrueSquared(Class<T> checkerClass) throws Exception {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(256);

        PrimesListChecker checker = checkerClass.newInstance();
        Assertions.assertTrue(checker.isAnyCompoundInList(list));
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, -1, 0})
    void testMTIllegalArgument(int threadsAmount) {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(256);

        PrimesListCheckerMT checker = new PrimesListCheckerMT();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    checker.isAnyCompoundInList(list, threadsAmount);
                });
    }

    @Test
    void testMTListSizeLessThanThreadsAmount() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(256);

        PrimesListCheckerMT checker = new PrimesListCheckerMT();
        Assertions.assertTrue(checker.isAnyCompoundInList(list, 10));
    }

    @ParameterizedTest
    @ValueSource(classes = {PrimesListCheckerSequential.class, PrimesListCheckerPS.class})
    <T extends PrimesListChecker> void bigTest(Class<T> checkerClass) throws Exception {
        PrimesListChecker checker = checkerClass.newInstance();
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

    private static boolean isPrime(int n) {
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
