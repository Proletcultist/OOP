package ru.nsu.zenin.tester.dsl

import ru.nsu.zenin.tester.model.Course
import ru.nsu.zenin.tester.model.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Delegate {
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.mm.YYYY")
    private Course.Builder builder = new Course.Builder()

    def course(Closure c) {
        c.delegate = this
        c.resolveStrategy = groovy.lang.Closure.DELEGATE_FIRST()
        c()
    }

    def task(int id, String name, int maxScore, String softDeadline, String hardDeadline) {
        builder.withTask(new Task(id, name, maxScore, LocalDate.parse(softDeadline, dateFormatter), LocalDate.parse(hardDeadline, dateFormatter)))
    }

    def getCourse() {
        return builder.build()
    }
}
