package ru.nsu.zenin.expression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MulTest {

    @Test
    void equalsTest() {
        Mul a = new Mul(new Number(3), new Number(5));
        Mul b = new Mul(new Number(3), new Number(5));
        Mul c = new Mul(new Number(5), new Number(3));
        Mul d = new Mul(new Variable("b"), new Number(3));

        Assertions.assertTrue(a.equals(b));
        Assertions.assertTrue(a.equals(c));
        Assertions.assertFalse(a.equals(d));
    }

    @Test
    void hashCodeTest() {
        Mul a = new Mul(new Number(3), new Number(5));
        Mul b = new Mul(new Number(3), new Number(5));
        Mul c = new Mul(new Number(5), new Number(3));
        Mul d = new Mul(new Variable("b"), new Number(3));

        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertEquals(a.hashCode(), c.hashCode());
        Assertions.assertNotEquals(a.hashCode(), d.hashCode());
    }

    @Test
    void evalTest() {
        Mul a = new Mul(new Number(3), new Variable("a"));

        Assertions.assertEquals(a.eval("a = 10"), 30);
    }

    @Test
    void derivativeTest() {
        Mul a = new Mul(new Number(3), new Variable("a"));

        Assertions.assertEquals(
                a.derivative("b"),
                new Add(
                        new Mul(new Number(0), new Variable("a")),
                        new Mul(new Number(3), new Variable("a"))));
        Assertions.assertEquals(
                a.derivative("a"),
                new Add(
                        new Mul(new Number(0), new Variable("a")),
                        new Mul(new Number(3), new Number(1))));
    }

    @Test
    void simplifyTest() {
        Mul a = new Mul(new Number(2), new Number(2));
        Mul b = new Mul(new Mul(new Number(3), new Number(5)), new Number(2));

        Mul c = new Mul(new Add(new Number(0), new Number(0)), new Variable("a"));
        Mul d = new Mul(new Add(new Number(1), new Number(0)), new Variable("a"));
        Mul e = new Mul(new Variable("a"), new Add(new Number(1), new Number(0)));

        Assertions.assertEquals(a.simpify(), new Number(4));
        Assertions.assertEquals(b.simpify(), new Number(30));
        Assertions.assertEquals(c.simpify(), new Number(0));
        Assertions.assertEquals(d.simpify(), new Variable("a"));
        Assertions.assertEquals(e.simpify(), new Variable("a"));
    }
}
