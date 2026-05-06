package ru.nsu.zenin.tester.model;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Assignment {
    private final Task task;
    private LocalDate lastCommitDate;
    private boolean buildable = false;
    private boolean codestyleCompliant = false;
    private boolean hasDocs = false;
    private int testsPassed = 0;
    private int testsFailed = 0;
    private int testsSkipped = 0;
    private double score = 0;

    public void clearStatus() {
        buildable = false;
        hasDocs = false;
        codestyleCompliant = false;
        testsPassed = 0;
        testsFailed = 0;
        testsSkipped = 0;
        score = 0;
    }
}
