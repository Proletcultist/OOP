package ru.nsu.zenin.tester.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import ru.nsu.zenin.tester.model.Assignment;
import ru.nsu.zenin.tester.model.Checkpoint;
import ru.nsu.zenin.tester.model.Course;
import ru.nsu.zenin.tester.model.Group;
import ru.nsu.zenin.tester.model.Student;

public class ReportService {
    private ReportService() {}

    public static void reportAllAssignments(Course course) {
        System.out.println("<html>");
        System.out.println(
                "<head><style>table, th, td { border: 1px solid black; } th, td { padding: 10px } table { table-layout: fixed; margin: 10px auto; border-collapse: collapse; }</style></head>");
        System.out.println("<body>");

        for (Group g : course.getGroups()) {
            reportGroup(course, g);
        }

        System.out.println("</body>");
        System.out.println("</html>");
    }

    private static void reportGroup(Course course, Group g) {
        System.out.println("<h2>" + g.name() + "</h2>");
        for (Student s : g.students()) {
            reportStudent(course, s);
        }
    }

    private static void reportStudent(Course course, Student s) {
        System.out.println("<h3>" + s.getFullName() + "</h3>\n<hr>");
        System.out.println(
                "<table>\n"
                        + "\t<thead>\n"
                        + "\t\t<tr>\n"
                        + "\t\t\t<th scope=\"col\">Task id</th>\n"
                        + "\t\t\t<th scope=\"col\">Build</th>\n"
                        + "\t\t\t<th scope=\"col\">Code style</th>\n"
                        + "\t\t\t<th scope=\"col\">Docs</th>\n"
                        + "\t\t\t<th scope=\"col\">Tests</th>\n"
                        + "\t\t\t<th scope=\"col\">Score</th>\n"
                        + "\t\t</tr>\n"
                        + "\t</thead>");

        Map<Checkpoint, Double> checkpointScore = new HashMap<Checkpoint, Double>();
        for (Checkpoint c : course.getCheckpoints().values()) {
            checkpointScore.put(c, 0.0);
        }

        System.out.println("\t<tbody>");
        for (Assignment ass : s.getAssignments()) {
            Path docs_index =
                    Paths.get(CheckService.DOCS_DIR, s.getId(), ass.getTask().id(), "index.html")
                            .toAbsolutePath();

            double score = computeAssignmentScore(ass);

            for (Checkpoint check :
                    course.getCheckpoints()
                            .subMap(
                                    ass.getTask().hardDeadline(),
                                    false,
                                    course.getCheckpoints().lastKey(),
                                    true)
                            .values()) {
                checkpointScore.put(check, checkpointScore.get(check) + score);
            }

            System.out.println(
                    "\t<tr>\n"
                            + "\t\t<td>"
                            + "<a href=\""
                            + docs_index
                            + "\">"
                            + ass.getTask().id()
                            + "</a>"
                            + "</td>\n"
                            + "\t\t<td>"
                            + booleanToString(ass.isBuildable())
                            + "</td>\n"
                            + "\t\t<td>"
                            + booleanToString(ass.isCodestyleCompliant())
                            + "</td>\n"
                            + "\t\t<td>"
                            + booleanToString(ass.isHasDocs())
                            + "</td>\n"
                            + "\t\t<td>"
                            + ass.getTestsPassed()
                            + "/"
                            + ass.getTestsFailed()
                            + "/"
                            + ass.getTestsSkipped()
                            + "</td>\n"
                            + "\t\t<td>"
                            + score
                            + "</td>\n"
                            + "\t</tr>");
        }

        System.out.println("\t</tbody>");
        System.out.println("</table>");

        System.out.println(
                "<table>\n"
                        + "\t<tr>\n"
                        + "\t\t<th>Checkpoint</th>\n"
                        + "\t\t<th>Date</th>\n"
                        + "\t\t<th>Score</th>\n"
                        + "\t\t<th>Grade</th>\n"
                        + "\t</tr>");
        for (Map.Entry<Checkpoint, Double> e : checkpointScore.entrySet()) {
            System.out.println(
                    "\t<tr>\n"
                            + "\t\t<td>"
                            + e.getKey().id()
                            + "</td>\n"
                            + "\t\t<td>"
                            + e.getKey().date()
                            + "</td>\n"
                            + "\t\t<td>"
                            + e.getValue()
                            + "</td>\n"
                            + "\t\t<td>"
                            + course.getGradeScale().floorEntry(e.getValue()).getValue()
                            + "</td>\n"
                            + "\t</tr>");
        }
        System.out.println("</table>");
    }

    private static double computeAssignmentScore(Assignment ass) {
        if (!ass.isBuildable() || !ass.isCodestyleCompliant() || !ass.isHasDocs()) {
            return 0;
        }

        double deadlineFactor = 0.0;

        if (ass.getLastCommitDate().isBefore(ass.getTask().softDeadline())) {
            deadlineFactor += 0.5;
        }
        if (ass.getLastCommitDate().isBefore(ass.getTask().hardDeadline())) {
            deadlineFactor += 0.5;
        }

        return ass.getTask().maxScore() * deadlineFactor;
    }

    private static String booleanToString(boolean b) {
        return b ? "+" : "-";
    }
}
