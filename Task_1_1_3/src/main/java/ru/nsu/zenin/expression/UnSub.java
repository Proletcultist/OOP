package ru.nsu.zenin.expression;

import ru.nsu.zenin.assignment.Assignment;
import ru.nsu.zenin.assignment.exception.AssignmentException;

public class UnSub extends UnOperator {

    public UnSub(Expression operand) {
        super(operand);
    }

    public Expression derivative(String variable) {
        return new UnSub(operand.derivative(variable));
    }

    int eval(Assignment assignment) throws AssignmentException, ArithmeticException {
        return -operand.eval(assignment);
    }

    public Expression simpify() {
        try {
            Assignment emptyAssignment = new Assignment();
            return new Number(eval(emptyAssignment));
        } catch (AssignmentException e) {
        }

        return (Expression) clone();
    }

    @Override
    public String toString() {
        return "-(" + operand + ")";
    }

    @Override
    public int hashCode() {
        return operand.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UnSub)) {
            return false;
        }

        return ((UnSub) obj).getOperand().equals(operand);
    }

    @Override
    public Object clone() {
        return new UnSub((Expression) operand.clone());
    }
}
