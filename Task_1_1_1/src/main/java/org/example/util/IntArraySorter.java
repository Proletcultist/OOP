package org.example.util;

/** Collection of sorting algorithms for arrays (only heapsort for now) */
public class IntArraySorter {

  /**
   * Sort array with heapsort algorithm
   *
   * @param arr - Array to sort
   * @return Reference on sorted array (same as passed as argument)
   */
  public static int[] heapSort(int[] arr) {
    Heapifier.makeHeap(arr, arr.length);

    for (int i = arr.length - 1; i > 0; i--) {
      int tmp = arr[0];

      arr[0] = arr[i];
      arr[i] = tmp;

      Heapifier.siftDown(arr, i, 0);
    }

    return arr;
  }
}
