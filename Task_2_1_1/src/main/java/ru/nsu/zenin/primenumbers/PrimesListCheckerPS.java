package ru.nsu.zenin.primenumbers;

import java.util.List;

public class PrimesListCheckerPS implements PrimesListChecker {

    public boolean isAnyCompoundInList(List<Integer> list) {
        return list.parallelStream().anyMatch((i) -> !PrimeChecker.isPrime(i));
    }
}
