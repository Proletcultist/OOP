package ru.nsu.zenin.expression;

import ru.nsu.zenin.assignment.Assignment;

public class Variable extends Expression {

    private final String name;

    public Variable(String name) {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Invalid variable name");
        }
        this.name = name;
    }

    public Expression derivative(String variable) {
        if (variable.equals(name)) {
            return new Number(1);
        } else {
            return (Expression) clone();
        }
    }

    int eval(Assignment assignment) {
        return assignment.getValue(this);
    }

    public Expression simpify() {
        return (Expression) clone();
    }

    public String getName() {
        return name;
    }

    @Override
    public Object clone() {
        return new Variable(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Variable)) {
            return false;
        }

        return ((Variable) obj).getName().equals(this.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static boolean isValidName(String name) {
        return name.length() != 0 && !name.contains(" ") && !Character.isDigit(name.charAt(0));
    }
}
