package ru.nsu.zenin.lexer.token;

import java.util.function.Function;
import ru.nsu.zenin.expression.Expression;
import ru.nsu.zenin.expression.UnOperator;

public class UnOperatorToken extends OperatorToken {

    private final Function<Expression, UnOperator> constructor;

    public <T extends UnOperator> UnOperatorToken(
            int priority, Associativity associativity, Class<T> clazz) {
        super(priority, associativity);

        this.constructor =
                (operand) -> {
                    try {
                        return clazz.getConstructor(Expression.class).newInstance(operand);
                    } catch (Exception e) {
                        throw new RuntimeException("Unexpected exception occured");
                    }
                };
    }

    public Expression getExpression(Expression operand) {
        return constructor.apply(operand);
    }
}
