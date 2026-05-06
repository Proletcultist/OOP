package ru.nsu.zenin.tester.dsl;

import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.tester.model.Task;

class TaskDelegateTests {
    @Test
    void test() throws Exception {
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        GroovyShell shell = new GroovyShell(cc);

        DelegatingScript script =
                (DelegatingScript)
                        shell.parse(getClass().getClassLoader().getResource("task.groovy").toURI());

        TaskDelegate d = new TaskDelegate();
        script.setDelegate(d);

        script.run();

        LocalDate date = LocalDate.of(2026, 5, 23);
        LocalDate date2 = LocalDate.of(2026, 1, 23);
        Task task = new Task("id", "name", 100, date, date2);

        List<Task> tasks = new ArrayList<Task>();
        tasks.add(task);

        Assertions.assertEquals(d.getCapturedTasks(), tasks);
    }
}
