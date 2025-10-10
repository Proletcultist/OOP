package ru.nsu.zenin.expression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VariableTest {

    @Test
    void equalsTest() {
        Variable a = new Variable(new String("a"));
        Variable b = new Variable(new String("a"));
        Variable c = new Variable(new String("b"));

        Assertions.assertTrue(a.equals(b));
        Assertions.assertFalse(a.equals(c));
    }

    @Test
    void hashCodeTest() {
        Variable a = new Variable(new String("a"));
        Variable b = new Variable(new String("a"));
        Variable c = new Variable(new String("b"));

        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a.hashCode(), c.hashCode());
    }

    @Test
    void evalTest() {
        Variable a = new Variable("a");

        Assertions.assertEquals(a.eval("a = 10"), 10);
    }

    @Test
    void derivativeTest() {
        Variable a = new Variable("a");

        Assertions.assertEquals(a.derivative("b"), new Number(0));
        Assertions.assertEquals(a.derivative("a"), new Number(1));
    }

    @Test
    void simplifyTest() {
        Variable a = new Variable("a");

        Assertions.assertEquals(a.simplify(), new Variable("a"));
    }

    @Test
    void invalidNameTest() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Variable a = new Variable("()");
                });
    }
}
