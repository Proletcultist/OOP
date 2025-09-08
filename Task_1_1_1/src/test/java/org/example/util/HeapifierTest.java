package org.example.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HeapifierTest {

  @Test
  public void testRandomArray() {
    testHeapification(new int[] {1, 2, 3, 4});
  }

  @Test
  public void testEmpty() {
    testHeapification(new int[] {});
  }

  @Test
  public void testSingleElement() {
    testHeapification(new int[] {1});
  }

  @Test
  public void testEqualElements() {
    testHeapification(new int[] {1, 1, 2, 3, 2, 9, 0, 9, 9});
  }

  @Test
  public void testNegative() {
    testHeapification(new int[] {-100, -90, 2, 3, 0, 9});
  }

  @Test
  public void testMaxMin() {
    testHeapification(new int[] {Integer.MAX_VALUE, 0, 12, -12, 9, Integer.MIN_VALUE});
  }

  private void testHeapification(int[] arr) {
    Heapifier.makeHeap(arr, arr.length);

    assertHeapInvariant(arr, arr.length);
  }

  private void assertHeapInvariant(int[] arr, int size) {
    for (int i = 0; i <= size / 2 - 1; i++) {
      int l = i * 2 + 1;
      int r = i * 2 + 2;

      if (l < size) {
        Assertions.assertTrue(arr[i] >= arr[l]);
      }
      if (r < size) {
        Assertions.assertTrue(arr[i] >= arr[r]);
      }
    }
  }
}
