package ru.nsu.zenin.tester.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Setter;
import ru.nsu.zenin.tester.service.logging.Logger;

public class Course {
    private List<Group> groups = new ArrayList<Group>();
    private Map<String, Student> studentById = new HashMap<String, Student>();
    private Map<String, Task> tasks = new HashMap<String, Task>();
    private TreeMap<LocalDate, Checkpoint> checkpoints = new TreeMap<LocalDate, Checkpoint>();
    @Setter private TreeMap<Integer, String> gradeScale = new TreeMap<Integer, String>();

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

    public void checkAllAssignments() throws Exception {
        for (Group g : groups) {
            for (Student s : g.students()) {
                s.checkAllAssignments();
            }
        }
    }

    public void reportAllAssignments() {}

    @Override
    public String toString() {
        return "Tasks: "
                + tasks.toString()
                + "\nGrade Scale: "
                + gradeScale.toString()
                + "\nCheckpoints: "
                + checkpoints.toString()
                + "\nGroups: "
                + groups.toString();
    }
}
