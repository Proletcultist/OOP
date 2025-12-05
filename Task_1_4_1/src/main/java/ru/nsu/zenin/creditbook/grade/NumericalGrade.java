package ru.nsu.zenin.creditbook.grade;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NumericalGrade implements Grade {
    EXCELLENT(5),
    GOOD(4),
    SATISFACTORY(3);

    private final int asInt;
}
