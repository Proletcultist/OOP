package ru.nsu.zenin.expression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubTest {

    @Test
    void equalsTest() {
        Sub a = new Sub(new Number(3), new Number(5));
        Sub b = new Sub(new Number(3), new Number(5));
        Sub c = new Sub(new Number(5), new Number(3));
        Sub d = new Sub(new Variable("b"), new Number(3));

        Assertions.assertTrue(a.equals(b));
        Assertions.assertFalse(a.equals(c));
        Assertions.assertFalse(a.equals(d));
    }

    @Test
    void hashCodeTest() {
        Sub a = new Sub(new Number(3), new Number(5));
        Sub b = new Sub(new Number(3), new Number(5));
        Sub c = new Sub(new Number(5), new Number(3));
        Sub d = new Sub(new Variable("b"), new Number(3));

        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a.hashCode(), c.hashCode());
        Assertions.assertNotEquals(a.hashCode(), d.hashCode());
    }

    @Test
    void evalTest() {
        Sub a = new Sub(new Number(3), new Variable("a"));
        Sub b = new Sub(new Variable("a"), new Number(3));

        Assertions.assertEquals(a.eval("a = 10"), -7);
        Assertions.assertEquals(b.eval("a = 10"), 7);
    }

    @Test
    void derivativeTest() {
        Sub a = new Sub(new Number(3), new Variable("a"));

        Assertions.assertEquals(a.derivative("b"), new Sub(new Number(0), new Variable("a")));
        Assertions.assertEquals(a.derivative("a"), new Sub(new Number(0), new Number(1)));
    }

    @Test
    void simplifyTest() {
        Sub a = new Sub(new Number(2), new Number(2));
        Sub b = new Sub(new Sub(new Number(3), new Number(5)), new Number(2));
        Sub c =
                new Sub(
                        new Add(new Variable("a"), new Variable("b")),
                        new Add(new Variable("b"), new Variable("a")));

        Assertions.assertEquals(a.simpify(), new Number(0));
        Assertions.assertEquals(b.simpify(), new Number(-4));
        Assertions.assertEquals(c.simpify(), new Number(0));
    }
}
