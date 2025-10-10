package ru.nsu.zenin.expression;

import ru.nsu.zenin.assignment.Assignment;

public class Number extends Expression {

    private final int value;

    public Number(int value) {
        this.value = value;
    }

    public Expression derivative(String variable) {
        return new Number(0);
    }

    int eval(Assignment assignment) {
        return value;
    }

    public Expression simplify() {
        return (Expression) clone();
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object clone() {
        return new Number(value);
    }

    @Override
    public String toString() {
        if (value >= 0) {
            return String.valueOf(value);
        } else {
            return "(" + String.valueOf(value) + ")";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Number)) {
            return false;
        }

        return ((Number) obj).getValue() == this.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
