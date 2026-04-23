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

    public void reportAllAssignments() {
        System.out.println("<html><body>");
        for (Group g : groups) {
            reportGroup(g);
        }
        System.out.println("</body></html>");
    }

    private void reportGroup(Group g) {
        System.out.println("<h1>" + g.name() + "</h1>");
        for (Student s : g.students()) {
            reportStudent(s);
        }
    }

    private void reportStudent(Student s) {
        System.out.println("<h2>" + s.getFullName() + "</h2>\n</hr>");
        System.out.println("<table>\n" + 
                           "\t<tr>\n" +
                           "\t\t<th>Task id</th>\n" +
                           "\t\t<th>Build</th>\n" +
                           "\t\t<th>Code style</th>\n" +
                           "\t\t<th>Docs</tr>\n" +
                           "\t\t<th>Tests</th>\n" +
                           "\t\t<th>Score</th>\n" +
                           "\t</tr>");

        Map<Checkpoint, Integer> checkpointScore = new HashMap<Checkpoint, Integer>();
        for (Checkpoint c : checkpoints.values()) {
            checkpointScore.put(c, 0);
        }

        for (Assignment ass : s.getAssignments()) {
            int score = computeAssignmentScore(ass);

            Checkpoint check = checkpoints.higherEntry(ass.getTask().hardDeadline()).getValue();
            checkpointScore.put(check, checkpointScore.get(check) + score);

            System.out.println("\t<tr>\n" + 
                               "\t\t<td>" + ass.getTask().id() + "</td>\n" +
                               "\t\t<td>" + booleanToString(ass.isBuildable()) + "</td>\n" +
                               "\t\t<td>" + booleanToString(ass.isCodestyleCompliant()) + "</td>\n" +
                               "\t\t<td>" + booleanToString(ass.isHasDocs()) + "</td>\n" +
                               "\t\t<td>" + ass.getTestsPassed() + "/" + ass.getTestsFailed() + "/" + ass.getTestsSkipped() + "</td>\n" +
                               "\t\t<td>" + score + "</td>\n" +
                               "\t</tr>");
        }

        System.out.println("</table>");

        System.out.println("<table>\n" + 
                           "\t<tr>\n" +
                           "\t\t<th>Checkpoint</th>\n" +
                           "\t\t<th>Date</th>\n" +
                           "\t\t<th>Score</th>\n" +
                           "\t</tr>");
        for (Map.Entry<Checkpoint, Integer> e : checkpointScore.entrySet()) {
            System.out.println("\t<tr>\n" + 
                               "\t\t<td>" + e.getKey().id() + "</td>\n" +
                               "\t\t<td>" + e.getKey().date() + "</td>\n" +
                               "\t\t<td>" + e.getValue() + "</td>\n" +
                               "\t</tr>");
        }
        System.out.println("</table>");
    }

    private int computeAssignmentScore(Assignment ass) {
        return 0;
    }

    private String booleanToString(boolean b) {
        return b ? "+" : "-";
    }

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
