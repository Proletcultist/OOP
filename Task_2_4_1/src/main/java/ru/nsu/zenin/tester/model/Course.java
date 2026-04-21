package ru.nsu.zenin.tester.model;

import java.util.List;
import java.util.ArrayList;

public record Course(List<Task> tasks) {
    public class Builder {
        List<Task> tasks = new ArrayList<Task>();

        public Builder withTask(Task t) {
            tasks.add(t);
            return Builder.this;
        }

        public Course build() {
            return new Course(tasks);
        }
    }
}
