package ru.nsu.zenin.tester.model;

public record Assignment(Task task, boolean buildable, boolean hasDocs, boolean codestyleCompliat, int testsPassed, int testsFailed) {}
