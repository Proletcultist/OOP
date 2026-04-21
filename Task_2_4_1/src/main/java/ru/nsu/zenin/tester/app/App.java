package ru.nsu.zenin.tester.app;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.nio.file.Paths;
import ru.nsu.zenin.tester.dsl.Delegate;

public class App {
    public static void main(String[] args) throws Exception {
        GroovyShell shell = new GroovyShell();
        Delegate d = new Delegate();

        Script script = shell.parse(Paths.get(args[0]).toFile());
        script.setDelegate(d);
        script.setResolveStrategy(groovy.lang.Closure.DELEGATE_FIRST);
        script.run();

        System.out.println(d.getCourse().toString());
    }
}
