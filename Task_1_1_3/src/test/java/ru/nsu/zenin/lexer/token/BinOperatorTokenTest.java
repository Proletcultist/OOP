package ru.nsu.zenin.lexer.token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.expression.Add;
import ru.nsu.zenin.expression.Expression;
import ru.nsu.zenin.expression.Number;

class BinOperatorTokenTest {

    @Test
    void test() {
        BinOperatorToken binOp =
                new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class);

        Expression leftOperand = new Number(12);
        Expression rightOperand = new Number(3);

        Expression add = binOp.getExpression(leftOperand, rightOperand);

        Assertions.assertEquals(add.eval(""), 15);
    }
}
