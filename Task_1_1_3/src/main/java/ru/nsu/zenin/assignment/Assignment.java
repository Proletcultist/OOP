package ru.nsu.zenin.assignment;

import java.util.HashMap;
import java.util.Map;
import ru.nsu.zenin.assignment.exception.AssignmentException;
import ru.nsu.zenin.expression.Variable;

public class Assignment {

    private final Map<Variable, Integer> mapping = new HashMap<Variable, Integer>();

    public void bind(Variable variable, int number) {
        mapping.put(variable, number);
    }

    public void unbind(Variable variable) {
        mapping.remove(variable);
    }

    public int getValue(Variable variable) {
        try {
            return mapping.get(variable);
        } catch (NullPointerException e) {
            throw new AssignmentException("Variable \"" + variable.getName() + "\" is unbound");
        }
    }
}
