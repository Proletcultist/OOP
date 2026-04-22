package ru.nsu.zenin.tester.app;

import groovy.lang.GroovyShell;
import groovy.lang.MissingMethodException;
import groovy.util.DelegatingScript;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import org.codehaus.groovy.control.CompilerConfiguration;
import java.time.format.DateTimeParseException;
import ru.nsu.zenin.tester.dsl.DslScriptDelegate;

public class App {
    public static void main(String[] args) throws Exception {
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        GroovyShell shell = new GroovyShell(cc);

        DelegatingScript script = (DelegatingScript) shell.parse(Paths.get(args[0]).toFile());
        DslScriptDelegate d = new DslScriptDelegate();
        script.setDelegate(d);
        try {
            script.run();
        }
        catch (Exception e) {
            System.err.println("[\033[31mError\033[0m] " + e.getMessage());
            System.exit(-1);
        }

        System.out.println(d.getCourse().toString());

        d.getCourse().checkAllAssignments();
    }
}
