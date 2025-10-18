package ru.nsu.zenin.expression;

import java.util.Objects;
import ru.nsu.zenin.assignment.Assignment;
import ru.nsu.zenin.assignment.exception.AssignmentException;
import ru.nsu.zenin.expression.exception.EvaluationException;

public class Div extends BinOperator {

    public Div(Expression leftOperand, Expression rightOperand) {
        super(leftOperand, rightOperand);
    }

    public Expression derivative(String variable) {
        return new Div(
                new Sub(
                        new Mul(
                                leftOperand.derivative(variable),
                                (Expression) rightOperand.clone()),
                        new Mul(
                                (Expression) leftOperand.clone(),
                                rightOperand.derivative(variable))),
                new Mul((Expression) rightOperand.clone(), (Expression) rightOperand.clone()));
    }

    int eval(Assignment assignment) throws AssignmentException, EvaluationException {
        if (rightOperand.equals(new Number(0))) {
            throw new EvaluationException("Devision by zero");
        }
        return leftOperand.eval(assignment) / rightOperand.eval(assignment);
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
        return "(" + leftOperand + " / " + rightOperand + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Div)) {
            return false;
        }

        return ((Div) obj).getLeftOperand().equals(leftOperand)
                && ((Div) obj).getRightOperand().equals(rightOperand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftOperand, rightOperand);
    }

    @Override
    public Object clone() {
        return new Div((Expression) leftOperand.clone(), (Expression) rightOperand.clone());
    }
}
