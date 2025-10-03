package ru.nsu.zenin.assignment;

import java.util.Arrays;
import ru.nsu.zenin.expression.Variable;

public class AssignmentParser {

    private AssignmentParser() {}

    public static Assignment parse(String assignment) {
        Assignment ret = new Assignment();

        if (assignment.isEmpty()) {
            return ret;
        }

        String[] splited = assignment.split(";");

        Arrays.stream(splited)
                .map(str -> parseSingleAssignment(str))
                .forEach(
                        singleAssignment -> {
                            ret.bind(singleAssignment.name(), singleAssignment.value());
                        });

        return ret;
    }

    private static SingleAssignment parseSingleAssignment(String str) {

        String[] splited = str.split("=");
        if (splited.length != 2) {
            throw new IllegalArgumentException("Expected one '=' character in single assignment");
        }

        splited[0] = splited[0].trim();
        splited[1] = splited[1].trim();

        if (!Variable.isValidName(splited[0])) {
            throw new IllegalArgumentException("Invalid name: \"" + splited[0] + "\"");
        }

        int val = 0;
        try {
            val = Integer.valueOf(splited[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: \"" + splited[1] + "\"");
        }

        return new SingleAssignment(new Variable(splited[0]), val);
    }

    private static record SingleAssignment(Variable name, int value) {}
    ;
}
