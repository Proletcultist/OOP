package ru.nsu.zenin.tester.dsl

import java.net.URL;
import ru.nsu.zenin.tester.model.Student;

class StudentDelegate {
    List<Student> capturedStudents = []

    def student(String id, String fullName, String ghRepo) {
        capturedStudents << new Student(id, fullName, new URL(ghRepo))
    }
}
