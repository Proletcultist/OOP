package ru.nsu.zenin.expression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DivTest {

    @Test
    void equalsTest() {
        Div a = new Div(new Number(3), new Number(5));
        Div b = new Div(new Number(3), new Number(5));
        Div c = new Div(new Number(5), new Number(3));
        Div d = new Div(new Variable("b"), new Number(3));

        Assertions.assertTrue(a.equals(b));
        Assertions.assertFalse(a.equals(c));
        Assertions.assertFalse(a.equals(d));
    }

    @Test
    void hashCodeTest() {
        Div a = new Div(new Number(3), new Number(5));
        Div b = new Div(new Number(3), new Number(5));
        Div c = new Div(new Number(5), new Number(3));
        Div d = new Div(new Variable("b"), new Number(3));

        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a.hashCode(), c.hashCode());
        Assertions.assertNotEquals(a.hashCode(), d.hashCode());
    }

    @Test
    void evalTest() {
        Div a = new Div(new Number(30), new Variable("a"));
        Div b = new Div(new Variable("a"), new Number(3));
        Div c = new Div(new Number(3), new Number(0));

        Assertions.assertEquals(a.eval("a = 6"), 5);
        Assertions.assertEquals(b.eval("a = 6"), 2);
        Assertions.assertThrows(
                ArithmeticException.class,
                () -> {
                    int res = c.eval("");
                });
    }

    @Test
    void derivativeTest() {
        Div a = new Div(new Number(3), new Variable("a"));

        Assertions.assertEquals(
                a.derivative("b"),
                new Div(
                        new Sub(
                                new Mul(new Number(0), new Variable("a")),
                                new Mul(new Number(3), new Number(0))),
                        new Mul(new Variable("a"), new Variable("a"))));
        Assertions.assertEquals(
                a.derivative("a"),
                new Div(
                        new Sub(
                                new Mul(new Number(0), new Variable("a")),
                                new Mul(new Number(3), new Number(1))),
                        new Mul(new Variable("a"), new Variable("a"))));
    }

    @Test
    void simplifyTest() {
        Div a = new Div(new Number(2), new Number(2));
        Div b = new Div(new Div(new Number(3), new Number(5)), new Number(2));
        Div c = new Div(new Variable("a"), new Number(2));

        Assertions.assertEquals(a.simplify(), new Number(1));
        Assertions.assertEquals(b.simplify(), new Number(0));
        Assertions.assertEquals(c.simplify(), new Div(new Variable("a"), new Number(2)));
    }
}
