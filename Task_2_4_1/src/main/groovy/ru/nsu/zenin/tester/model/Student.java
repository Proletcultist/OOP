package ru.nsu.zenin.tester.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Student {
    private final String id;
    private final String fullName;
    private final URL ghRepo;

    private List<Assignment> assignments = new ArrayList<Assignment>();

    public void assign(Task task) {
        assignments.add(new Assignment(task));
    }
}
