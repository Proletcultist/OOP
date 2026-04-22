package ru.nsu.zenin.tester.dsl

import ru.nsu.zenin.tester.model.Checkpoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CheckpointDelegate {
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    List<Checkpoint> capturedCheckpoints = []

    def checkpoint(String id, String date) {
        capturedCheckpoints << new Checkpoint(id, LocalDate.parse(date, dateFormatter))
    }
}
