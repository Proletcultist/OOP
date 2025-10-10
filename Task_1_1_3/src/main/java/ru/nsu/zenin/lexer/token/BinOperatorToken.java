package ru.nsu.zenin.lexer.token;

import java.util.function.BiFunction;
import ru.nsu.zenin.expression.BinOperator;
import ru.nsu.zenin.expression.Expression;

public class BinOperatorToken extends OperatorToken {

    private final BiFunction<Expression, Expression, BinOperator> constructor;

    public <T extends BinOperator> BinOperatorToken(
            int priority, Associativity associativity, Class<T> clazz) {
        super(priority, associativity);

        this.constructor =
                (leftOperand, rightOperand) -> {
                    try {
                        return clazz.getConstructor(Expression.class, Expression.class)
                                .newInstance(leftOperand, rightOperand);
                    } catch (Exception e) {
                        throw new RuntimeException("Unexpected exception occured");
                    }
                };
    }

    public Expression getExpression(Expression leftOperand, Expression rightOperand) {
        return constructor.apply(leftOperand, rightOperand);
    }
}
