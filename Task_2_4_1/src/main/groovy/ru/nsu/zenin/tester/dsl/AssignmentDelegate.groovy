package ru.nsu.zenin.tester.dsl

import ru.nsu.zenin.tester.model.Course

class AssignmentDelegate {
    private final Course course

    AssignmentDelegate(Course course) {
        this.course = course
    }

    def assign(String studentId, String... taskId) {
        def student = course.getStudent(studentId)
        taskId.each { 
            def task = course.getTask(it)
            student.assign(task)
        }
    }
}
