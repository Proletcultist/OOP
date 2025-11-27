package ru.nsu.zenin.creditbook.grade;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NumericalGradeTest {
    @Test
    void getAsIntTest() {
        Assertions.assertEquals(NumericalGrade.EXCELLENT.getAsInt(), 5);
        Assertions.assertEquals(NumericalGrade.GOOD.getAsInt(), 4);
        Assertions.assertEquals(NumericalGrade.SATISFACTORY.getAsInt(), 3);
    }
}
