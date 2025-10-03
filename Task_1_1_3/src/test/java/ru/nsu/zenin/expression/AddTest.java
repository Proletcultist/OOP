package ru.nsu.zenin.expression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AddTest {

    @Test
    void equalsTest() {
        Add a = new Add(new Number(3), new Number(5));
        Add b = new Add(new Number(3), new Number(5));
        Add c = new Add(new Number(5), new Number(3));
        Add d = new Add(new Variable("b"), new Number(3));

        Assertions.assertTrue(a.equals(b));
        Assertions.assertTrue(a.equals(c));
        Assertions.assertFalse(a.equals(d));
    }

    @Test
    void hashCodeTest() {
        Add a = new Add(new Number(3), new Number(5));
        Add b = new Add(new Number(3), new Number(5));
        Add c = new Add(new Number(5), new Number(3));
        Add d = new Add(new Variable("b"), new Number(3));

        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertEquals(a.hashCode(), c.hashCode());
        Assertions.assertNotEquals(a.hashCode(), d.hashCode());
    }

    @Test
    void evalTest() {
        Add a = new Add(new Number(3), new Variable("a"));

        Assertions.assertEquals(a.eval("a = 10"), 13);
    }

    @Test
    void derivativeTest() {
        Add a = new Add(new Number(3), new Variable("a"));

        Assertions.assertEquals(a.derivative("b"), new Add(new Number(0), new Variable("a")));
        Assertions.assertEquals(a.derivative("a"), new Add(new Number(0), new Number(1)));
    }

    @Test
    void simplifyTest() {
        Add a = new Add(new Number(2), new Number(2));
        Add b = new Add(new Add(new Number(3), new Number(5)), new Number(2));

        Assertions.assertEquals(a.simpify(), new Number(4));
        Assertions.assertEquals(b.simpify(), new Number(10));
    }
}
