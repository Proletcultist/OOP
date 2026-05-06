package ru.nsu.zenin.tester.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GroupTests {
    @Test
    void test() throws Exception {
        List<Student> students = new ArrayList<Student>();
        students.add(new Student("id", "name", new URL("http://example.com/")));

        Group g = new Group("name", students);

        Assertions.assertEquals(g.name(), "name");
        Assertions.assertEquals(g.students(), students);
    }
}
