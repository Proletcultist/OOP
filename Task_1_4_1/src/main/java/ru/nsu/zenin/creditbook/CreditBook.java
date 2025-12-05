package ru.nsu.zenin.creditbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.Setter;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import ru.nsu.zenin.creditbook.grade.Grade;
import ru.nsu.zenin.creditbook.grade.NumericalGrade;

public class CreditBook {

    private static final double EXCELLENT_PRECENTAGE_FOR_RED_DIPLOMA = 0.75;

    @Setter private FinancingType financingType;
    private Optional<NumericalGrade> qualificationWorkGrade;
    private final List<List<Entry>> entriesPerSemester = new ArrayList<List<Entry>>();

    public CreditBook(FinancingType financingType) {
        entriesPerSemester.add(new ArrayList<Entry>());
        this.financingType = financingType;
        this.qualificationWorkGrade = Optional.empty();
    }

    public void promoteToNextSemester() {
        entriesPerSemester.add(new ArrayList<Entry>());
    }

    public void setQualificationWorkGrade(NumericalGrade grade) {
        qualificationWorkGrade = Optional.of(grade);
    }

    public void addEntry(Subject subj, ControlType controlType, Grade grade) {
        addEntry(subj, controlType, grade, entriesPerSemester.size() - 1);
    }

    public void addEntry(Subject subj, ControlType controlType, Grade grade, int semester) {
        entriesPerSemester.get(semester).add(new Entry(subj, controlType, grade));
    }

    public double getAverageGrade() {

        return Stream.concat(
                        entriesPerSemester.stream().flatMap(List::stream).map(Entry::grade),
                        Stream.of(qualificationWorkGrade)
                                .filter(Optional::isPresent)
                                .map(Optional::get))
                .filter(grade -> grade instanceof NumericalGrade)
                .map(grade -> (NumericalGrade) grade)
                .mapToInt(NumericalGrade::getAsInt)
                .average()
                .orElse(0.0);
    }

    public boolean canSwitchToGovernmentalFinancing() {
        return financingType != FinancingType.GOVERNMENTAL
                && !StreamSupport.stream(
                                Spliterators.spliteratorUnknownSize(
                                        new ReverseListIterator<List<Entry>>(entriesPerSemester),
                                        Spliterator.ORDERED | Spliterator.NONNULL),
                                false)
                        .skip(1)
                        .limit(2)
                        .flatMap(List::stream)
                        .filter(entry -> entry.controlType() == ControlType.EXAM)
                        .map(Entry::grade)
                        .filter(grade -> grade instanceof NumericalGrade)
                        .map(grade -> (NumericalGrade) grade)
                        .anyMatch(grade -> grade == NumericalGrade.SATISFACTORY);
    }

    public boolean canGetRedDiploma() {
        if (!qualificationWorkGrade.isPresent()
                || qualificationWorkGrade.get() != NumericalGrade.EXCELLENT) {
            return false;
        }

        if (entriesPerSemester.stream()
                .flatMap(List::stream)
                .map(Entry::grade)
                .filter(grade -> grade instanceof NumericalGrade)
                .map(grade -> (NumericalGrade) grade)
                .anyMatch(grade -> grade == NumericalGrade.SATISFACTORY)) {
            return false;
        }

        AtomicInteger excellentsAmount = new AtomicInteger(0);
        AtomicInteger numericGradesAmount = new AtomicInteger(0);

        entriesPerSemester.stream()
                .flatMap(List::stream)
                .map(Entry::grade)
                .filter(grade -> grade instanceof NumericalGrade)
                .map(grade -> (NumericalGrade) grade)
                .forEach(
                        grade -> {
                            numericGradesAmount.incrementAndGet();
                            if (grade == NumericalGrade.EXCELLENT) {
                                excellentsAmount.incrementAndGet();
                            }
                        });

        if ((double) excellentsAmount.get() / (double) numericGradesAmount.get()
                < EXCELLENT_PRECENTAGE_FOR_RED_DIPLOMA) {
            return false;
        }

        return true;
    }

    public boolean canGetIncreasedScholarship() {
        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(
                                new ReverseListIterator<List<Entry>>(entriesPerSemester),
                                Spliterator.ORDERED | Spliterator.NONNULL),
                        false)
                .skip(1)
                .limit(1)
                .flatMap(List::stream)
                .map(Entry::grade)
                .filter(grade -> grade instanceof NumericalGrade)
                .map(grade -> (NumericalGrade) grade)
                .allMatch(grade -> grade == NumericalGrade.EXCELLENT);
    }

    private record Entry(Subject subject, ControlType controlType, Grade grade) {}
}
