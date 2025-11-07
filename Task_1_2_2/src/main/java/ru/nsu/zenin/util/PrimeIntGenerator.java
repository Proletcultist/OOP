package ru.nsu.zenin.util;

public class PrimeIntGenerator {
    private PrimeIntGenerator() {}

    public static int nextPrime(int n) {
        while (true) {
            n++;

            if (n < 2) {
                return 2;
            }

            if (isPrime(n)) {
                return n;
            }
        }
    }

    private static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }

        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
