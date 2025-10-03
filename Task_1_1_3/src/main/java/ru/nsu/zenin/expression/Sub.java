package ru.nsu.zenin.expression;

import java.util.Objects;
import ru.nsu.zenin.assignment.Assignment;
import ru.nsu.zenin.assignment.exception.AssignmentException;

public class Sub extends BinOperator {

    public Sub(Expression leftOperand, Expression rightOperand) {
        super(leftOperand, rightOperand);
    }

    public Expression derivative(String variable) {
        return new Sub(leftOperand.derivative(variable), rightOperand.derivative(variable));
    }

    int eval(Assignment assignment) throws AssignmentException {
        return leftOperand.eval(assignment) - rightOperand.eval(assignment);
    }

    public Expression simpify() {
        try {
            Assignment emptyAssignment = new Assignment();
            return new Number(eval(emptyAssignment));
        } catch (AssignmentException e) {
        }

        Expression leftSimplificated = leftOperand.simpify(),
                rightSimplificated = rightOperand.simpify();

        if (leftSimplificated.equals(rightSimplificated)) {
            return new Number(0);
        }

        return (Expression) clone();
    }

    @Override
    public String toString() {
        return "(" + leftOperand + " - " + rightOperand + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Sub)) {
            return false;
        }

        return ((Sub) obj).getLeftOperand().equals(leftOperand)
                && ((Sub) obj).getRightOperand().equals(rightOperand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftOperand, rightOperand);
    }

    @Override
    public Object clone() {
        return new Sub((Expression) leftOperand.clone(), (Expression) rightOperand.clone());
    }
}
