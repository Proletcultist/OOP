package ru.nsu.zenin.tester.dsl

import org.codehaus.groovy.control.CompilerConfiguration;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap
import ru.nsu.zenin.tester.model.Course

class DslScriptDelegate {
    private Course course = new Course()

    private final Path workingDirectory;

    DslScriptDelegate() {
        workingDirectory = Paths.get(".")
    }

    DslScriptDelegate(Path workingDirectory) {
        this.workingDirectory = workingDirectory
    }

    def include(String path) {
        def file = workingDirectory.resolve(path).toFile()
        if (!file.exists()) throw new FileNotFoundException("Config not found: $path")

        def cc = new CompilerConfiguration()
        cc.setScriptBaseClass(DelegatingScript.class.getName())
        def shell = new groovy.lang.GroovyShell(cc)
        def script = shell.parse(file)
        script.setDelegate(this)
        script.run()
    }

    def checkpoints(Closure c) {
        def d = new CheckpointDelegate()

        c.delegate = d
        c.resolveStrategy = groovy.lang.Closure.DELEGATE_FIRST
        c()

        d.capturedCheckpoints.each { course.addCheckpoint(it) }
    }

    def settings(Closure c) {
        c.delegate = this
        c.resolveStrategy = groovy.lang.Closure.DELEGATE_FIRST
        c()
    }

    def setGradeScale(Map<Double, String> gradeScale) {
        TreeMap<Double, String> converted = new TreeMap<Double, String>();
        gradeScale.each { key, value -> 
            if (key instanceof Number) {
                converted.put(key.doubleValue(), value.toString());
            }
            else {
                throw new IllegalArgumentException("Grade scale must be numbers, got " + key.class);
            }
        }
        course.setGradeScale(converted);
    }

    def groups(Closure c) {
        def d = new GroupDelegate()

        c.delegate = d
        c.resolveStrategy = groovy.lang.Closure.DELEGATE_FIRST
        c()

        d.capturedGroups.each { course.addGroup(it) }
    }

    def assignments(Closure c) {
        def d = new AssignmentDelegate(course)

        c.delegate = d
        c.resolveStrategy = groovy.lang.Closure.DELEGATE_FIRST
        c()
    }

    def tasks(Closure c) {
        def d = new TaskDelegate()

        c.delegate = d
        c.resolveStrategy = groovy.lang.Closure.DELEGATE_FIRST
        c()

        d.capturedTasks.each { course.addTask(it) }
    }

    def Course getCourse() {
        return course
    }
}
