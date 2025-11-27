package ru.nsu.zenin.creditbook.grade;

public enum NumericalGrade implements Grade {
    EXCELLENT(5),
    GOOD(4),
    SATISFACTORY(3);

    private final int asInt;

    private NumericalGrade(int asInt) {
        this.asInt = asInt;
    }

    public int getAsInt() {
        return asInt;
    }
}
