package org.example.util;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntArraySorterTest {

  @Test
  public void testRandomArray() {
    var arr = IntArraySorter.heapSort(new int[] {4, 5, 1, 3});

    Assertions.assertArrayEquals(arr, new int[] {1, 3, 4, 5});
  }

  @Test
  public void testEmptyArray() {
    var arr = IntArraySorter.heapSort(new int[] {});

    Assertions.assertArrayEquals(arr, new int[] {});
  }

  @Test
  public void testSingleElementedArray() {
    var arr = IntArraySorter.heapSort(new int[] {2});

    Assertions.assertArrayEquals(arr, new int[] {2});
  }

  @Test
  public void testSortOfCopy() {
    var arr = new int[] {1, 7, 3, 9};

    var sortedArr = IntArraySorter.heapSort(Arrays.copyOf(arr, arr.length));

    Assertions.assertArrayEquals(arr, new int[] {1, 7, 3, 9});
    Assertions.assertArrayEquals(sortedArr, new int[] {1, 3, 7, 9});
  }

  @Test
  public void testEqualElements() {
    var arr = IntArraySorter.heapSort(new int[] {9, 1, 1, 2, 3, 2});

    Assertions.assertArrayEquals(arr, new int[] {1, 1, 2, 2, 3, 9});
  }

  @Test
  public void testAlreadySorted() {
    var arr = IntArraySorter.heapSort(new int[] {1, 2, 3, 4, 5});

    Assertions.assertArrayEquals(arr, new int[] {1, 2, 3, 4, 5});
  }

  @Test
  public void testNegative() {
    var arr = IntArraySorter.heapSort(new int[] {-12, 0, 9, -100, 92});

    Assertions.assertArrayEquals(arr, new int[] {-100, -12, 0, 9, 92});
  }

  @Test
  public void testMinMax() {
    var arr =
        IntArraySorter.heapSort(new int[] {Integer.MAX_VALUE, 0, 127, 512, -10, Integer.MIN_VALUE});

    Assertions.assertArrayEquals(
        arr, new int[] {Integer.MIN_VALUE, -10, 0, 127, 512, Integer.MAX_VALUE});
  }
}
