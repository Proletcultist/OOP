package ru.nsu.zenin.primenumbers;

import java.util.UUID;

public record Task(UUID taskId, int[] chunk) {}
