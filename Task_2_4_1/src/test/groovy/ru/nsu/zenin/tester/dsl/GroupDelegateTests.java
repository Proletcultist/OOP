package ru.nsu.zenin.tester.dsl;

import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.tester.model.Group;
import ru.nsu.zenin.tester.model.Student;

class GroupDelegateTests {
    @Test
    void test() throws Exception {
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        GroovyShell shell = new GroovyShell(cc);

        DelegatingScript script =
                (DelegatingScript)
                        shell.parse(
                                getClass().getClassLoader().getResource("group.groovy").toURI());

        GroupDelegate d = new GroupDelegate();
        script.setDelegate(d);

        script.run();

        List<Student> students = new ArrayList<Student>();
        students.add(new Student("id", "name", new URL("http://example.com/")));
        students.add(new Student("id2", "name2", new URL("http://example.com/2")));

        Group g = new Group("group", students);

        Assertions.assertEquals(d.getCapturedGroups().size(), 1);
        Assertions.assertEquals(d.getCapturedGroups().get(0), g);
    }
}
