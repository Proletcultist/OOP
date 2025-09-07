package org.example.util;

/** Utils for making and maintaining heap on int array */
public class Heapifier {

  /**
   * Make heap on existing array
   *
   * @param arr - array, on which heap will be built
   * @param size - size of a heap on array
   */
  public static void makeHeap(int[] arr, int size) {
    // Sift down all nodes, starting from last non-leaf node
    for (int i = size / 2 - 1; i >= 0; i--) {
      siftDown(arr, size, i);
    }
  }

  /**
   * Sift down node, restoring invariant of heap
   *
   * @param arr - array, pepresenting heap
   * @param size - size of a heap
   * @param index - index of node to sift down
   */
  public static void siftDown(int[] arr, int size, int index) {
    int l = index * 2 + 1;
    int r = index * 2 + 2;

    int largest = index;

    if (l < size && arr[l] > arr[largest]) {
      largest = l;
    }

    if (r < size && arr[r] > arr[largest]) {
      largest = r;
    }

    if (largest != index) {
      int tmp = arr[largest];

      arr[largest] = arr[index];
      arr[index] = tmp;

      siftDown(arr, size, largest);
    }
  }
}
