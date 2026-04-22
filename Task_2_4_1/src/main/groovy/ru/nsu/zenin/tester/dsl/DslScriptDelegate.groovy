package ru.nsu.zenin.tester.dsl

import ru.nsu.zenin.tester.model.Course

class DslScriptDelegate {
    private Course course = new Course()

    def include(String path) {
    }

    def checkpoints(Closure c) {
        def d = new CheckpointDelegate()

        c.delegate = d
        c.resolveStrategy = groovy.lang.Closure.DELEGATE_FIRST
        c()

        d.capturedCheckpoints.each { course.addCheckpoint(it) }
    }

    def settings(Closure c) {
        c.delegate = course
        c.resolveStrategy = groovy.lang.Closure.DELEGATE_FIRST
        c()
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

    def getCourse() {
        return course
    }
}
