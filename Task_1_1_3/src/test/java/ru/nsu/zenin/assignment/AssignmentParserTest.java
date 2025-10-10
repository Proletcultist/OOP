package ru.nsu.zenin.assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.expression.Variable;

class AsignmentParserTest {

    @Test
    void simpleTest() {
        Assignment ass = new Assignment();

        ass.bind(new Variable("a"), 100);
        Assertions.assertEquals(ass.getValue(new Variable("a")), 100);
    }

    @Test
    void parseTest() {
        Assignment ass = AssignmentParser.parse("a = 100; b = 2");

        Assertions.assertEquals(ass.getValue(new Variable("a")), 100);
        Assertions.assertEquals(ass.getValue(new Variable("b")), 2);
    }

    @Test
    void invalidAssignmentTest() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Assignment ass = AssignmentParser.parse("a = = 100; b = 2");
                });

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Assignment ass = AssignmentParser.parse("a = 100 b = 2");
                });

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Assignment ass = AssignmentParser.parse("10a = 100; b = 2");
                });

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Assignment ass = AssignmentParser.parse("a = 100; b = a");
                });

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Assignment ass = AssignmentParser.parse("a = 100;; b = 2");
                });
    }
}
