package ru.nsu.zenin.tester.app;

import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import java.nio.file.Paths;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import org.codehaus.groovy.control.CompilerConfiguration;
import ru.nsu.zenin.tester.dsl.DslScriptDelegate;
import ru.nsu.zenin.tester.service.logging.Logger;

public class App {
    public static void main(String[] args) throws Exception {
        Logger.init(new BufferedWriter(new OutputStreamWriter(System.err)));

        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        GroovyShell shell = new GroovyShell(cc);

        DelegatingScript script = (DelegatingScript) shell.parse(Paths.get(args[0]).toFile());
        DslScriptDelegate d = new DslScriptDelegate();
        script.setDelegate(d);
        try {
            script.run();
        } catch (Exception e) {
            Logger.log(Logger.LogLevel.ERROR, e.getMessage());
            System.exit(-1);
        }

        //System.out.println(d.getCourse().toString());

        d.getCourse().checkAllAssignments();

        d.getCourse().reportAllAssignments();
    }
}
