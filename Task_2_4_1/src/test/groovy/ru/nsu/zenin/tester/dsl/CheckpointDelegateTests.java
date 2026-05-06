package ru.nsu.zenin.tester.dsl;

import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.tester.model.Checkpoint;

class CheckpointDelegateTests {
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
                                        .getResource("checkpoint.groovy")
                                        .toURI());

        CheckpointDelegate d = new CheckpointDelegate();
        script.setDelegate(d);

        script.run();

        LocalDate date = LocalDate.of(2026, 5, 23);
        Checkpoint c = new Checkpoint("checkpoint", date);

        List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
        checkpoints.add(c);

        Assertions.assertEquals(d.getCapturedCheckpoints(), checkpoints);
    }
}
