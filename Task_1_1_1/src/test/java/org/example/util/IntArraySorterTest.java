package org.example.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests for IntArraySorter class. */
public class IntArraySorterTest {

    @Test
    public void testRandomArray() {
        var arr = new int[] {4, 5, 1, 3};
        IntArraySorter.heapSort(arr);

        Assertions.assertArrayEquals(arr, new int[] {1, 3, 4, 5});
    }

    @Test
    public void testEmptyArray() {
        var arr = new int[] {};
        IntArraySorter.heapSort(arr);

        Assertions.assertArrayEquals(arr, new int[] {});
    }

    @Test
    public void testSingleElementedArray() {
        var arr = new int[] {2};
        IntArraySorter.heapSort(arr);

        Assertions.assertArrayEquals(arr, new int[] {2});
    }

    @Test
    public void testEqualElements() {
        var arr = new int[] {9, 1, 1, 2, 3, 2};
        IntArraySorter.heapSort(arr);

        Assertions.assertArrayEquals(arr, new int[] {1, 1, 2, 2, 3, 9});
    }

    @Test
    public void testAlreadySorted() {
        var arr = new int[] {1, 2, 3, 4, 5};
        IntArraySorter.heapSort(arr);

        Assertions.assertArrayEquals(arr, new int[] {1, 2, 3, 4, 5});
    }

    @Test
    public void testNegative() {
        var arr = new int[] {-12, 0, 9, -100, 92};
        IntArraySorter.heapSort(arr);

        Assertions.assertArrayEquals(arr, new int[] {-100, -12, 0, 9, 92});
    }

    @Test
    public void testMinMax() {
        var arr = new int[] {Integer.MAX_VALUE, 0, 127, 512, -10, Integer.MIN_VALUE};
        IntArraySorter.heapSort(arr);

        Assertions.assertArrayEquals(
                arr, new int[] {Integer.MIN_VALUE, -10, 0, 127, 512, Integer.MAX_VALUE});
    }

    @Test
    public void testNull() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    IntArraySorter.heapSort(null);
                });
    }
}
