package ru.nsu.zenin.expression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UnSubTest {

    @Test
    void equalsTest() {
        UnSub a = new UnSub(new Number(3));
        UnSub b = new UnSub(new Number(3));
        UnSub c = new UnSub(new Number(5));

        Assertions.assertTrue(a.equals(b));
        Assertions.assertFalse(a.equals(c));
    }

    @Test
    void hashCodeTest() {
        UnSub a = new UnSub(new Number(3));
        UnSub b = new UnSub(new Number(3));
        UnSub c = new UnSub(new Number(5));

        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a.hashCode(), c.hashCode());
    }

    @Test
    void evalTest() {
        UnSub a = new UnSub(new Number(3));
        UnSub b = new UnSub(new Variable("a"));

        Assertions.assertEquals(a.eval("a = 10"), -3);
        Assertions.assertEquals(b.eval("a = 10"), -10);
    }

    @Test
    void derivativeTest() {
        UnSub a = new UnSub(new Variable("a"));

        Assertions.assertEquals(a.derivative("b"), new UnSub(new Variable("a")));
        Assertions.assertEquals(a.derivative("a"), new UnSub(new Number(1)));
    }

    @Test
    void simplifyTest() {
        UnSub a = new UnSub(new Number(2));

        Assertions.assertEquals(a.simpify(), new Number(-2));
    }
}
