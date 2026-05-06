package ru.nsu.zenin.tester.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import ru.nsu.zenin.tester.model.Assignment;
import ru.nsu.zenin.tester.model.Checkpoint;
import ru.nsu.zenin.tester.model.Course;
import ru.nsu.zenin.tester.model.Group;
import ru.nsu.zenin.tester.model.Student;

public class ReportService {
    private ReportService() {}

    public static void reportAllAssignments(Path root, Course course) {
        System.out.println("<html>");
        System.out.println(
                "<head><style>table, th, td { border: 1px solid black; } th, td { padding: 10px } table { table-layout: fixed; margin: 10px auto; border-collapse: collapse; }</style></head>");
        System.out.println("<body>");

        for (Group g : course.getGroups()) {
            reportGroup(root, course, g);
        }

        System.out.println("</body>");
        System.out.println("</html>");
    }

    private static void reportGroup(Path root, Course course, Group g) {
        System.out.println("<h2>" + g.name() + "</h2>");
        for (Student s : g.students()) {
            reportStudent(root, course, s);
        }
    }

    private static void reportStudent(Path root, Course course, Student s) {
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

        System.out.println("\t<tbody>");
        for (Assignment ass : s.getAssignments()) {
            Path docs_index =
                    Paths.get(
                                    root.toString(),
                                    CheckService.DOCS_DIR,
                                    s.getId(),
                                    ass.getTask().id(),
                                    "index.html")
                            .toAbsolutePath();

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
                            + ass.getScore()
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
        for (Checkpoint c : course.getCheckpoints().values()) {
            Double score = s.getCheckpointScores().get(c);
            System.out.println(
                    "\t<tr>\n"
                            + "\t\t<td>"
                            + c.id()
                            + "</td>\n"
                            + "\t\t<td>"
                            + c.date()
                            + "</td>\n"
                            + "\t\t<td>"
                            + (score == null ? "-" : score)
                            + "</td>\n"
                            + "\t\t<td>"
                            + (score == null || course.getGradeScale() == null
                                    ? "-"
                                    : course.getGradeScale().floorEntry(score).getValue())
                            + "</td>\n"
                            + "\t</tr>");
        }
        System.out.println("</table>");
    }

    private static String booleanToString(boolean b) {
        return b ? "+" : "-";
    }
}
