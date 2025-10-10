package ru.nsu.zenin.expression;

public abstract class UnOperator extends Expression {
    protected Expression operand;

    public UnOperator(Expression operand) {
        this.operand = operand;
    }

    public Expression getOperand() {
        return operand;
    }
}
