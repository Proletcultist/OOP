package ru.nsu.zenin.lexer.token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.assignment.exception.AssignmentParserException;
import ru.nsu.zenin.expression.Expression;
import ru.nsu.zenin.expression.Number;
import ru.nsu.zenin.expression.UnSub;
import ru.nsu.zenin.expression.exception.EvaluationException;

class UnOperatorTokenTest {

    @Test
    void test() throws AssignmentParserException, EvaluationException {
        UnOperatorToken unOp =
                new UnOperatorToken(1, OperatorToken.Associativity.LEFT, UnSub.class);

        Expression operand = new Number(12);

        Expression unSub = unOp.getExpression(operand);

        Assertions.assertEquals(unSub.eval(""), -12);
    }
}
