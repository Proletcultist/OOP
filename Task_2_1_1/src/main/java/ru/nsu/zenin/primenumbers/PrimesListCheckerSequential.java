package ru.nsu.zenin.primenumbers;

import java.util.List;

public class PrimesListCheckerSequential implements PrimesListChecker {
    public boolean isAnyCompoundInList(List<Integer> list) {
        for (int i : list) {
            if (!PrimeChecker.isPrime(i)) {
                return true;
            }
        }
        return false;
    }
}
