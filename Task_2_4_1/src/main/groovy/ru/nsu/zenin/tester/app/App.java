package ru.nsu.zenin.tester.app;

import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.codehaus.groovy.control.CompilerConfiguration;
import ru.nsu.zenin.tester.dsl.DslScriptDelegate;
import ru.nsu.zenin.tester.model.Group;
import ru.nsu.zenin.tester.model.Student;
import ru.nsu.zenin.tester.service.CheckService;
import ru.nsu.zenin.tester.service.EvaluationService;
import ru.nsu.zenin.tester.service.ReportService;
import ru.nsu.zenin.tester.service.logging.Logger;

public class App {
    private static String CONFIG_NAME = "config.groovy";

    public static void main(String[] args) throws Exception {
        Logger.init(new BufferedWriter(new OutputStreamWriter(System.err)));

        Path root = Paths.get(".").toRealPath();
        if (args.length != 0) {
            try {
                root = Paths.get(args[0]);
            } catch (InvalidPathException e) {
                Logger.log(Logger.LogLevel.ERROR, "Malformed path: \"" + args[0] + "\"");
                System.exit(-1);
            }
        }

        Path config = root.resolve(CONFIG_NAME);
        if (!Files.exists(config)) {
            Logger.log(Logger.LogLevel.ERROR, "Cannot find config file " + config.toAbsolutePath());
            System.exit(-1);
        }

        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        GroovyShell shell = new GroovyShell(cc);

        DelegatingScript script = (DelegatingScript) shell.parse(config.toFile());
        DslScriptDelegate d = new DslScriptDelegate(root);
        script.setDelegate(d);
        try {
            script.run();
        } catch (Exception e) {
            Logger.log(Logger.LogLevel.ERROR, e.getMessage());
            System.exit(-1);
        }

        CheckService.checkAllAssignments(root, d.getCourse());
        for (Group g : d.getCourse().getGroups()) {
            for (Student s : g.students()) {
                EvaluationService.evaluateStudent(d.getCourse(), s);
            }
        }
        ReportService.reportAllAssignments(root, d.getCourse());
    }
}
