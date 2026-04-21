package ru.nsu.zenin.tester.app;

import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import java.nio.file.Paths;
import org.codehaus.groovy.control.CompilerConfiguration;
import ru.nsu.zenin.tester.dsl.DslScriptDelegate;

public class App {
    public static void main(String[] args) throws Exception {
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        GroovyShell shell = new GroovyShell(cc);

        DelegatingScript script = (DelegatingScript) shell.parse(Paths.get(args[0]).toFile());

        DslScriptDelegate d = new DslScriptDelegate();

        script.setDelegate(d);
        script.run();

        System.out.println(d.getConfig().toString());
    }
}
