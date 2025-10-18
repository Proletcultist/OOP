package ru.nsu.zenin.expression;

import ru.nsu.zenin.assignment.Assignment;
import ru.nsu.zenin.assignment.AssignmentParser;
import ru.nsu.zenin.assignment.exception.AssignmentException;
import ru.nsu.zenin.assignment.exception.AssignmentParserException;
import ru.nsu.zenin.expression.exception.EvaluationException;

public abstract class Expression implements Cloneable {

    public void print() {
        System.out.println(this.toString());
    }

    public abstract Expression derivative(String variable);

    public int eval(String assignment)
            throws AssignmentException, EvaluationException, AssignmentParserException {
        return eval(AssignmentParser.parse(assignment));
    }

    abstract int eval(Assignment assignment) throws AssignmentException, EvaluationException;

    public abstract Expression simplify() throws EvaluationException;

    @Override
    public abstract String toString();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract Object clone();
}
