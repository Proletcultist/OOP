package ru.nsu.zenin.expression;

import ru.nsu.zenin.assignment.Assignment;
import ru.nsu.zenin.assignment.AssignmentParser;
import ru.nsu.zenin.assignment.exception.AssignmentException;

public abstract class Expression implements Cloneable {

    public void print() {
        System.out.println(this.toString());
    }

    public abstract Expression derivative(String variable);

    public int eval(String assignment) throws AssignmentException, ArithmeticException {
        return eval(AssignmentParser.parse(assignment));
    }

    abstract int eval(Assignment assignment) throws AssignmentException, ArithmeticException;

    public abstract Expression simplify();

    @Override
    public abstract String toString();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract Object clone();
}
