package ru.nsu.zenin.creditbook;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.creditbook.grade.NumericalGrade;
import ru.nsu.zenin.creditbook.grade.PassGrade;

class CreditBookTest {
    @Test
    void averageTest() {
        CreditBook book = new CreditBook(FinancingType.PRIVATE);

        book.addEntry(Subject.OOP, ControlType.CREDIT, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, PassGrade.PASS);

        book.promoteToNextSemester();

        book.addEntry(Subject.OOP, ControlType.CREDIT, NumericalGrade.GOOD);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, NumericalGrade.GOOD);

        Assertions.assertEquals(book.getAverageGrade(), 4.5);
    }

    @Test
    void canSwitchToGovernmentalFinancingTest() {
        CreditBook book = new CreditBook(FinancingType.PRIVATE);

        book.addEntry(Subject.OOP, ControlType.EXAM, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, PassGrade.PASS);

        book.promoteToNextSemester();

        book.addEntry(Subject.OOP, ControlType.EXAM, NumericalGrade.GOOD);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.GOOD);

        book.promoteToNextSemester();

        Assertions.assertTrue(book.canSwitchToGovernmentalFinancing());
    }

    @Test
    void canSwitchToGovernmentalFinancingTest2() {
        CreditBook book = new CreditBook(FinancingType.PRIVATE);

        book.addEntry(Subject.OOP, ControlType.CREDIT, NumericalGrade.SATISFACTORY);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, PassGrade.PASS);

        book.promoteToNextSemester();

        book.addEntry(Subject.OOP, ControlType.EXAM, NumericalGrade.GOOD);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.GOOD);

        book.promoteToNextSemester();

        Assertions.assertTrue(book.canSwitchToGovernmentalFinancing());
    }

    @Test
    void canSwitchToGovernmentalFinancingTest3() {
        CreditBook book = new CreditBook(FinancingType.PRIVATE);

        book.addEntry(Subject.OOP, ControlType.EXAM, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, PassGrade.PASS);

        book.promoteToNextSemester();

        Assertions.assertTrue(book.canSwitchToGovernmentalFinancing());
    }

    @Test
    void cantSwitchToGovernmentalFinancingTest() {
        CreditBook book = new CreditBook(FinancingType.PRIVATE);
        book.setFinancingType(FinancingType.GOVERNMENTAL);

        book.addEntry(Subject.OOP, ControlType.EXAM, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, PassGrade.PASS);

        book.promoteToNextSemester();

        Assertions.assertFalse(book.canSwitchToGovernmentalFinancing());
    }

    @Test
    void cantSwitchToGovernmentalFinancingTest2() {
        CreditBook book = new CreditBook(FinancingType.PRIVATE);

        book.addEntry(Subject.OOP, ControlType.EXAM, NumericalGrade.SATISFACTORY);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, PassGrade.PASS);

        book.promoteToNextSemester();

        Assertions.assertFalse(book.canSwitchToGovernmentalFinancing());
    }

    @Test
    void canGetRedDiplomaTest() {
        CreditBook book = new CreditBook(FinancingType.PRIVATE);

        book.addEntry(Subject.OOP, ControlType.EXAM, NumericalGrade.GOOD);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, PassGrade.PASS);

        book.promoteToNextSemester();

        book.addEntry(Subject.OOP, ControlType.CREDIT, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);

        book.promoteToNextSemester();

        book.setQualificationWorkGrade(NumericalGrade.EXCELLENT);

        Assertions.assertTrue(book.canGetRedDiploma());
    }

    @Test
    void cantGetRedDiplomaTest2() {
        CreditBook book = new CreditBook(FinancingType.PRIVATE);

        book.addEntry(Subject.OOP, ControlType.EXAM, NumericalGrade.GOOD);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, PassGrade.PASS);

        book.promoteToNextSemester();

        book.addEntry(Subject.OOP, ControlType.CREDIT, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);

        book.promoteToNextSemester();

        book.setQualificationWorkGrade(NumericalGrade.GOOD);

        Assertions.assertFalse(book.canGetRedDiploma());
    }

    @Test
    void cantGetRedDiplomaTest3() {
        CreditBook book = new CreditBook(FinancingType.PRIVATE);

        book.addEntry(Subject.OOP, ControlType.EXAM, NumericalGrade.GOOD);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, PassGrade.PASS);

        book.promoteToNextSemester();

        book.addEntry(Subject.OOP, ControlType.CREDIT, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);

        book.promoteToNextSemester();

        Assertions.assertFalse(book.canGetRedDiploma());
    }

    @Test
    void cantGetRedDiplomaTest4() {
        CreditBook book = new CreditBook(FinancingType.PRIVATE);

        book.addEntry(Subject.OOP, ControlType.EXAM, NumericalGrade.SATISFACTORY);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, PassGrade.PASS);

        book.promoteToNextSemester();

        book.addEntry(Subject.OOP, ControlType.CREDIT, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);

        book.promoteToNextSemester();

        book.setQualificationWorkGrade(NumericalGrade.EXCELLENT);

        Assertions.assertFalse(book.canGetRedDiploma());
    }

    @Test
    void cantGetRedDiplomaTest5() {
        CreditBook book = new CreditBook(FinancingType.PRIVATE);

        book.addEntry(Subject.OOP, ControlType.EXAM, NumericalGrade.GOOD);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.GOOD);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, PassGrade.PASS);

        book.promoteToNextSemester();

        book.addEntry(Subject.OOP, ControlType.CREDIT, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);

        book.promoteToNextSemester();

        book.setQualificationWorkGrade(NumericalGrade.EXCELLENT);

        Assertions.assertFalse(book.canGetRedDiploma());
    }

    @Test
    void canGetIncreasedScholarshipTest() {
        CreditBook book = new CreditBook(FinancingType.PRIVATE);

        book.addEntry(Subject.OOP, ControlType.EXAM, NumericalGrade.GOOD);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.GOOD);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, PassGrade.PASS);

        book.promoteToNextSemester();

        book.addEntry(Subject.OOP, ControlType.CREDIT, NumericalGrade.EXCELLENT);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);

        book.promoteToNextSemester();

        Assertions.assertTrue(book.canGetIncreasedScholarship());
    }

    @Test
    void cantGetIncreasedScholarshipTest() {
        CreditBook book = new CreditBook(FinancingType.PRIVATE);

        book.addEntry(Subject.OOP, ControlType.EXAM, NumericalGrade.GOOD);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.GOOD);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.CREDIT, PassGrade.PASS);

        book.promoteToNextSemester();

        book.addEntry(Subject.OOP, ControlType.CREDIT, NumericalGrade.GOOD);
        book.addEntry(Subject.OPERATING_SYSTEMS, ControlType.EXAM, NumericalGrade.EXCELLENT);

        book.promoteToNextSemester();

        Assertions.assertFalse(book.canGetIncreasedScholarship());
    }
}
