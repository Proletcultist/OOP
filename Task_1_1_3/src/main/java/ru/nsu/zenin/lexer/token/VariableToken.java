package ru.nsu.zenin.lexer.token;

import ru.nsu.zenin.expression.Variable;

public class VariableToken {
    private final String name;

    public VariableToken(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Variable asVariableExpr() {
        return new Variable(name);
    }
}
