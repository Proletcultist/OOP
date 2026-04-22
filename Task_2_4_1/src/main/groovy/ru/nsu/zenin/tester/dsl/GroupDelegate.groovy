package ru.nsu.zenin.tester.dsl

import ru.nsu.zenin.tester.model.Group;

class GroupDelegate {
    List<Group> capturedGroups = []

    def group(String name, Closure c) {
        def d = new StudentDelegate()

        c.delegate = d
        c.resolveStrategy = groovy.lang.Closure.DELEGATE_FIRST
        c()

        capturedGroups << new Group(name, d.capturedStudents)
    }
}
