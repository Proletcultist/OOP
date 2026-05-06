package ru.nsu.zenin.tester.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Course {
    @Getter private List<Group> groups = new ArrayList<Group>();
    private Map<String, Student> studentById = new HashMap<String, Student>();
    private Map<String, Task> tasks = new HashMap<String, Task>();

    @Getter
    private TreeMap<LocalDate, Checkpoint> checkpoints = new TreeMap<LocalDate, Checkpoint>();

    @Getter @Setter private TreeMap<Double, String> gradeScale = new TreeMap<Double, String>();

    public void addTask(Task t) {
        tasks.put(t.id(), t);
    }

    public void addCheckpoint(Checkpoint c) {
        checkpoints.put(c.date(), c);
    }

    public void addGroup(Group g) {
        groups.add(g);
        for (Student s : g.students()) {
            studentById.put(s.getId(), s);
        }
    }

    public Student getStudent(String studentId) {
        return studentById.get(studentId);
    }

    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }
}
