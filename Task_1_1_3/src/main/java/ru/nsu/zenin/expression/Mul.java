package ru.nsu.zenin.expression;

import java.util.Objects;
import ru.nsu.zenin.assignment.Assignment;
import ru.nsu.zenin.assignment.exception.AssignmentException;
import ru.nsu.zenin.expression.exception.EvaluationException;

public class Mul extends BinOperator {

    public Mul(Expression leftOperand, Expression rightOperand) {
        super(leftOperand, rightOperand);
    }

    public Expression derivative(String variable) {
        return new Add(
                new Mul(leftOperand.derivative(variable), (Expression) rightOperand.clone()),
                new Mul((Expression) leftOperand.clone(), rightOperand.derivative(variable)));
    }

    int eval(Assignment assignment) throws AssignmentException, EvaluationException {
        return leftOperand.eval(assignment) * rightOperand.eval(assignment);
    }

    public Expression simplify() throws EvaluationException {
        try {
            Assignment emptyAssignment = new Assignment();
            return new Number(eval(emptyAssignment));
        } catch (AssignmentException e) {
        }

        Expression leftSimplificated = leftOperand.simplify(),
                rightSimplificated = rightOperand.simplify();

        Number one = new Number(1);
        Number zero = new Number(0);
        if (leftSimplificated.equals(one)) {
            return rightSimplificated;
        }
        if (rightSimplificated.equals(one)) {
            return leftSimplificated;
        }
        if (leftSimplificated.equals(zero) || rightSimplificated.equals(zero)) {
            return zero;
        }

        return (Expression) clone();
    }

    @Override
    public String toString() {
        return "(" + leftOperand + " * " + rightOperand + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Mul)) {
            return false;
        }

        return ((Mul) obj).getLeftOperand().equals(leftOperand)
                        && ((Mul) obj).getRightOperand().equals(rightOperand)
                || ((Mul) obj).getLeftOperand().equals(rightOperand)
                        && ((Mul) obj).getRightOperand().equals(leftOperand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftOperand, rightOperand) + Objects.hash(rightOperand, leftOperand);
    }

    @Override
    public Object clone() {
        return new Mul((Expression) leftOperand.clone(), (Expression) rightOperand.clone());
    }
}
