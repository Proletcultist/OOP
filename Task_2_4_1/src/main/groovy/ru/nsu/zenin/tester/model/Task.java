package ru.nsu.zenin.tester.model;

import java.time.LocalDate;
import lombok.Data;

public record Task(int id, String name, int maxScore, LocalDate softDeadline, LocalDate hardDeadline) {}
