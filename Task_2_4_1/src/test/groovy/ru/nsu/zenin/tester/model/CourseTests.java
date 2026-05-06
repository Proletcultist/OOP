package ru.nsu.zenin.tester.model;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CourseTests {
    @Test
    void test() throws Exception {
        Course course = new Course();

        LocalDate now = LocalDate.now();
        LocalDate notNow = now.plusDays(1);
        Task task = new Task("id", "name", 100, now, notNow);

        LocalDate date = LocalDate.now();
        Checkpoint c = new Checkpoint("id", date);

        List<Student> students = new ArrayList<Student>();
        students.add(new Student("id", "name", new URL("http://example.com/")));
        Group g = new Group("name", students);

        course.addTask(task);
        course.addCheckpoint(c);
        course.addGroup(g);

        TreeMap<Double, String> gradeScale = new TreeMap<Double, String>();
        gradeScale.put(2.0, "otlichno");

        course.setGradeScale(gradeScale);

        Assertions.assertEquals(course.getGroups().size(), 1);
        Assertions.assertEquals(course.getGroups().get(0), g);
        Assertions.assertEquals(course.getCheckpoints().size(), 1);
        Assertions.assertEquals(course.getCheckpoints().get(date), c);
        Assertions.assertEquals(course.getGradeScale(), gradeScale);
        Assertions.assertEquals(course.getStudent("id"), g.students().get(0));
        Assertions.assertEquals(course.getTask("id"), task);
    }
}
