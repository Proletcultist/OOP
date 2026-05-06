package ru.nsu.zenin.tester.dsl;

import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.tester.model.Checkpoint;
import ru.nsu.zenin.tester.model.Course;
import ru.nsu.zenin.tester.model.Group;
import ru.nsu.zenin.tester.model.Student;
import ru.nsu.zenin.tester.model.Task;

class DslTests {
    @Test
    void test() throws Exception {
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        GroovyShell shell = new GroovyShell(cc);

        DelegatingScript script =
                (DelegatingScript)
                        shell.parse(
                                getClass().getClassLoader().getResource("config.groovy").toURI());

        Course course = new Course();

        course.addTask(
                new Task(
                        "Task_1_1_1",
                        "Heap Sort",
                        2,
                        LocalDate.of(1999, 8, 20),
                        LocalDate.of(2025, 10, 1)));
        course.addTask(
                new Task(
                        "Task_1_1_2",
                        "Blackjack",
                        3,
                        LocalDate.of(2025, 12, 9),
                        LocalDate.of(2025, 11, 1)));
        course.addTask(
                new Task(
                        "Task_1_2_2",
                        "abobA",
                        1,
                        LocalDate.of(2025, 12, 9),
                        LocalDate.of(2026, 1, 1)));

        List<Student> students = new ArrayList<Student>();
        students.add(
                new Student(
                        "proletcultist",
                        "Зенин Матвей Вадимович",
                        new URL("https://github.com/Proletcultist/OOP")));
        students.add(
                new Student(
                        "chebupelka",
                        "Токарев Максим Константинович",
                        new URL("https://github.com/chebupelka332-pro/OOP")));
        Group g = new Group("24214", students);
        course.addGroup(g);

        course.addCheckpoint(new Checkpoint("midterm", LocalDate.of(2026, 1, 1)));
        course.addCheckpoint(new Checkpoint("final", LocalDate.of(2026, 6, 1)));

        TreeMap<Double, String> gradeScale = new TreeMap<Double, String>();
        gradeScale.put(90.0, "5");
        gradeScale.put(75.0, "4");
        gradeScale.put(60.0, "3");
        gradeScale.put(0.0, "2");
        course.setGradeScale(gradeScale);

        course.getStudent("proletcultist").assign(course.getTask("Task_1_1_1"));
        course.getStudent("proletcultist").assign(course.getTask("Task_1_1_2"));
        course.getStudent("proletcultist").assign(course.getTask("Task_1_2_2"));

        course.getStudent("chebupelka").assign(course.getTask("Task_1_1_1"));
        course.getStudent("chebupelka").assign(course.getTask("Task_1_2_2"));

        DslScriptDelegate d =
                new DslScriptDelegate(
                        Paths.get(getClass().getClassLoader().getResource("config.groovy").toURI())
                                .getParent());
        script.setDelegate(d);
        script.run();

        Assertions.assertEquals(d.getCourse(), course);
    }
}
