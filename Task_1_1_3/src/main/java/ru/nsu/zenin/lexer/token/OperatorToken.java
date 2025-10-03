package ru.nsu.zenin.lexer.token;

public abstract class OperatorToken {

    protected final int priority;
    protected final Associativity associativity;

    public OperatorToken(int priority, Associativity associativity) {
        this.priority = priority;
        this.associativity = associativity;
    }

    public int getPriority() {
        return priority;
    }

    public Associativity getAssociativity() {
        return associativity;
    }

    public enum Associativity {
        LEFT,
        RIGHT
    }
}
