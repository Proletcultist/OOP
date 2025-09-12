package org.example;

import java.util.Arrays;
import org.example.util.IntArraySorter;

public class Main{
	public static void main(String[] args){
		System.out.println("Example of using hapsort");

		var arr = new int[] {4, 3, 2, 1};

		System.out.print("Unsorted array: ");
		System.out.println(Arrays.toString(arr));

		IntArraySorter.heapSort(arr);
		
		System.out.print("Sorted array: ");
		System.out.println(Arrays.toString(arr));
	}
}
