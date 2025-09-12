package org.example.util;

/** Collection of sorting algorithms for arrays (only heapsort for now). */
public class IntArraySorter {

    private IntArraySorter() {}

    /**
     * Sort array with heapsort algorithm.
     *
     * @param arr - Array to sort
     */
    public static void heapSort(int[] arr) {
        if (arr == null) {
            throw new NullPointerException();
        }

        Heapifier.makeHeap(arr, arr.length);

        for (int i = arr.length - 1; i > 0; i--) {
            int tmp = arr[0];

            arr[0] = arr[i];
            arr[i] = tmp;

            Heapifier.siftDown(arr, i, 0);
        }
    }
}
