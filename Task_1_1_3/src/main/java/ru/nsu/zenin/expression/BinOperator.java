package ru.nsu.zenin.expression;

public abstract class BinOperator extends Operator {

    protected Expression leftOperand, rightOperand;

    public BinOperator(Expression leftOperand, Expression rightOperand) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    public Expression getLeftOperand() {
        return leftOperand;
    }

    public Expression getRightOperand() {
        return rightOperand;
    }
}
