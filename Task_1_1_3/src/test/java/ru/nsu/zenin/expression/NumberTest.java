package ru.nsu.zenin.expression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NumberTest {

    @Test
    void equalsTest() {
        Number a = new Number(3);
        Number b = new Number(3);
        Number c = new Number(4);

        Assertions.assertTrue(a.equals(b));
        Assertions.assertFalse(a.equals(c));
    }

    @Test
    void hashCodeTest() {
        Number a = new Number(3);
        Number b = new Number(3);
        Number c = new Number(4);

        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a.hashCode(), c.hashCode());
    }

    @Test
    void evalTest() {
        Number a = new Number(3);

        Assertions.assertEquals(a.eval(""), 3);
    }

    @Test
    void derivativeTest() {
        Number a = new Number(3);

        Assertions.assertEquals(a.derivative("a"), new Number(0));
    }

    @Test
    void simplifyTest() {
        Number a = new Number(3);

        Assertions.assertEquals(a.simpify(), new Number(3));
    }
}
