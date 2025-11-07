package ru.nsu.zenin.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PrimeIntGeneratorTest {

    @Test
    void test() {
        Assertions.assertEquals(PrimeIntGenerator.nextPrime(-100), 2);
        Assertions.assertEquals(PrimeIntGenerator.nextPrime(0), 2);
        Assertions.assertEquals(PrimeIntGenerator.nextPrime(1), 2);
        Assertions.assertEquals(PrimeIntGenerator.nextPrime(2), 3);
        Assertions.assertEquals(PrimeIntGenerator.nextPrime(3), 5);
        Assertions.assertEquals(PrimeIntGenerator.nextPrime(5), 7);
        Assertions.assertEquals(PrimeIntGenerator.nextPrime(7), 11);
        Assertions.assertEquals(PrimeIntGenerator.nextPrime(1337), 1361);
        Assertions.assertEquals(PrimeIntGenerator.nextPrime(Integer.MAX_VALUE), 2);
        Assertions.assertEquals(PrimeIntGenerator.nextPrime(Integer.MIN_VALUE), 2);
    }
}
