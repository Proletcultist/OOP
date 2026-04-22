package ru.nsu.zenin.tester.model;

import lombok.Data;

@Data
public class Assignment {
    private final Task task;
    private boolean buildable = false;
    private boolean codestyleCompliat = false;
    private boolean hasDocs = false;
    private int testsPassed = 0;
    private int testsFailed = 0;

    public void clearStatus() {
        buildable = false;
        hasDocs = false;
        codestyleCompliat = false;
        testsPassed = 0;
        testsFailed = 0;
    }
}
