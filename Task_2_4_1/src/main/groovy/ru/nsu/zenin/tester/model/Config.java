package ru.nsu.zenin.tester.model;

import lombok.Getter;
import java.util.List;
import java.util.ArrayList;

public class Config {
    @Getter
    private List<Task> tasks = new ArrayList<Task>();

    public void addTask(Task t) {
        tasks.add(t);
    }

    @Override
    public String toString() {
        return tasks.toString();
    }
}

