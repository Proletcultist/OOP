package ru.nsu.zenin.tester.dsl

import ru.nsu.zenin.tester.model.Course

class AssignmentDelegate {
    private final Course course

    AssignmentDelegate(Course course) {
        this.course = course
    }

    def assign(String studentId, String... taskId) {
        def student = course.getStudent(studentId)
        if (student == null) {
            throw new RuntimeException("No student with id \"" + studentId + "\" found")
        }

        taskId.each { 
            def task = course.getTask(it)
            if (task == null) {
                throw new RuntimeException("No task with id \"" + taskId + "\" found")
            }
            student.assign(task)
        }
    }
}
