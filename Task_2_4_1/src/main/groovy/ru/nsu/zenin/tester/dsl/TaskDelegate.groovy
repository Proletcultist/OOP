package ru.nsu.zenin.tester.dsl

import ru.nsu.zenin.tester.model.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TaskDelegate {
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    List<Task> capturedTasks = []

    def task(String id, String name, int score, String softDeadline, String hardDeadline) {
        capturedTasks << new Task(id, name, score, LocalDate.parse(softDeadline, dateFormatter), LocalDate.parse(hardDeadline, dateFormatter))
    }
}
