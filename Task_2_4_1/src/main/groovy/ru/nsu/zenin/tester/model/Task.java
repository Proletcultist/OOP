package ru.nsu.zenin.tester.model;

import java.time.LocalDate;

public record Task(String id, String name, int maxScore, LocalDate softDeadline, LocalDate hardDeadline) {}
