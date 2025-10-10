package ru.nsu.zenin.expression;

import java.util.Objects;
import ru.nsu.zenin.assignment.Assignment;
import ru.nsu.zenin.assignment.exception.AssignmentException;
import ru.nsu.zenin.expression.exception.EvaluationException;

public class Add extends BinOperator {

    public Add(Expression leftOperand, Expression rightOperand) {
        super(leftOperand, rightOperand);
    }

    public Expression derivative(String variable) {
        return new Add(leftOperand.derivative(variable), rightOperand.derivative(variable));
    }

    int eval(Assignment assignment) throws AssignmentException, EvaluationException {
        return leftOperand.eval(assignment) + rightOperand.eval(assignment);
    }

    public Expression simplify() throws EvaluationException {
        try {
            Assignment emptyAssignment = new Assignment();
            return new Number(eval(emptyAssignment));
        } catch (AssignmentException e) {
        }

        return (Expression) clone();
    }

    @Override
    public String toString() {
        return "(" + leftOperand + " + " + rightOperand + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Add)) {
            return false;
        }

        return ((Add) obj).getLeftOperand().equals(leftOperand)
                        && ((Add) obj).getRightOperand().equals(rightOperand)
                || ((Add) obj).getLeftOperand().equals(rightOperand)
                        && ((Add) obj).getRightOperand().equals(leftOperand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftOperand, rightOperand) + Objects.hash(rightOperand, leftOperand);
    }

    @Override
    public Object clone() {
        return new Add((Expression) leftOperand.clone(), (Expression) rightOperand.clone());
    }
}
