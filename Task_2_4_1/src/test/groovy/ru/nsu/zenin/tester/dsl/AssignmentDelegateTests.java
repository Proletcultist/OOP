package ru.nsu.zenin.tester.dsl;

import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.tester.model.Course;
import ru.nsu.zenin.tester.model.Group;
import ru.nsu.zenin.tester.model.Student;
import ru.nsu.zenin.tester.model.Task;

class AssignmentDelegateTests {
    @Test
    void test() throws Exception {
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        GroovyShell shell = new GroovyShell(cc);

        DelegatingScript script =
                (DelegatingScript)
                        shell.parse(
                                getClass()
                                        .getClassLoader()
                                        .getResource("assignment.groovy")
                                        .toURI());

        Course course = new Course();

        LocalDate now = LocalDate.now();
        LocalDate notNow = now.plusDays(1);
        Task task = new Task("task1", "name", 100, now, notNow);
        Task task2 = new Task("task2", "name2", 100, now, notNow);

        List<Student> students = new ArrayList<Student>();
        students.add(new Student("studentId", "name", new URL("http://example.com/")));
        Group g = new Group("name", students);

        course.addTask(task);
        course.addTask(task2);
        course.addGroup(g);

        AssignmentDelegate d = new AssignmentDelegate(course);
        script.setDelegate(d);

        script.run();

        Student st = course.getStudent("studentId");

        Assertions.assertEquals(st.getAssignments().get(0).getTask(), task);
        Assertions.assertEquals(st.getAssignments().get(1).getTask(), task2);
    }

    @Test
    void invalidTest() throws Exception {
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        GroovyShell shell = new GroovyShell(cc);

        DelegatingScript script =
                (DelegatingScript)
                        shell.parse(
                                getClass()
                                        .getClassLoader()
                                        .getResource("invalidAssignment.groovy")
                                        .toURI());

        Course course = new Course();

        AssignmentDelegate d = new AssignmentDelegate(course);
        script.setDelegate(d);

        Assertions.assertThrows(
                RuntimeException.class,
                () -> {
                    script.run();
                });
    }

    @Test
    void invalidTest2() throws Exception {
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        GroovyShell shell = new GroovyShell(cc);

        DelegatingScript script =
                (DelegatingScript)
                        shell.parse(
                                getClass()
                                        .getClassLoader()
                                        .getResource("invalidAssignment2.groovy")
                                        .toURI());

        Course course = new Course();

        List<Student> students = new ArrayList<Student>();
        students.add(new Student("studentId", "name", new URL("http://example.com/")));
        Group g = new Group("name", students);

        course.addGroup(g);

        AssignmentDelegate d = new AssignmentDelegate(course);
        script.setDelegate(d);

        Assertions.assertThrows(
                RuntimeException.class,
                () -> {
                    script.run();
                });
    }
}
