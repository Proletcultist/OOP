package ru.nsu.zenin.tester.dsl

import ru.nsu.zenin.tester.model.Config

class DslScriptDelegate {
    private Config config = new Config()

    def tasks(Closure c) {
        def d = new TaskDelegate()

        c.delegate = d
        c.resolveStrategy = groovy.lang.Closure.DELEGATE_FIRST
        c()

        d.capturedTasks.each { config.addTask(it) }
    }

    def getConfig() {
        return config
    }
}
