package ru.nsu.zenin.lexer.token;

import ru.nsu.zenin.expression.Number;

public class NumberToken {
    private final int value;

    public NumberToken(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public Number asNumberExpr() {
        return new Number(value);
    }
}
