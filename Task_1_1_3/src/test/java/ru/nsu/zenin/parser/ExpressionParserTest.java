package ru.nsu.zenin.parser;

import java.util.HashMap;
import java.util.InputMismatchException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.assignment.exception.AssignmentParserException;
import ru.nsu.zenin.expression.Add;
import ru.nsu.zenin.expression.Div;
import ru.nsu.zenin.expression.Mul;
import ru.nsu.zenin.expression.Sub;
import ru.nsu.zenin.expression.UnSub;
import ru.nsu.zenin.expression.exception.EvaluationException;
import ru.nsu.zenin.lexer.ExpressionLexer;
import ru.nsu.zenin.lexer.token.BinOperatorToken;
import ru.nsu.zenin.lexer.token.OperatorToken;
import ru.nsu.zenin.lexer.token.UnOperatorToken;
import ru.nsu.zenin.parser.exception.ParserException;

class ExpressionParserTest {

    @Test
    void test() throws EvaluationException, ParserException, AssignmentParserException {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));
        binOps.put('-', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Sub.class));
        binOps.put('*', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Mul.class));
        binOps.put('/', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Div.class));
        unOps.put('-', new UnOperatorToken(3, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("2 +2", binOps, unOps);

        Assertions.assertEquals(ExpressionParser.parse(lexer).eval(""), 4);
    }

    @Test
    void test2() throws EvaluationException, ParserException, AssignmentParserException {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));
        binOps.put('-', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Sub.class));
        binOps.put('*', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Mul.class));
        binOps.put('/', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Div.class));
        unOps.put('-', new UnOperatorToken(3, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("2+2*2", binOps, unOps);

        Assertions.assertEquals(ExpressionParser.parse(lexer).eval(""), 6);
    }

    @Test
    void test3() throws EvaluationException, ParserException, AssignmentParserException {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));
        binOps.put('-', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Sub.class));
        binOps.put('*', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Mul.class));
        binOps.put('/', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Div.class));
        unOps.put('-', new UnOperatorToken(3, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("(2+2)*2", binOps, unOps);

        Assertions.assertEquals(ExpressionParser.parse(lexer).eval(""), 8);
    }

    @Test
    void test4() throws EvaluationException, ParserException, AssignmentParserException {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));
        binOps.put('-', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Sub.class));
        binOps.put('*', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Mul.class));
        binOps.put('/', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Div.class));
        unOps.put('-', new UnOperatorToken(3, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("x + y", binOps, unOps);

        Assertions.assertEquals(ExpressionParser.parse(lexer).eval("x= 2; y    =   6"), 8);
    }

    @Test
    void test5() throws EvaluationException, ParserException, AssignmentParserException {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));
        binOps.put('-', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Sub.class));
        binOps.put('*', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Mul.class));
        binOps.put('/', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Div.class));
        unOps.put('-', new UnOperatorToken(3, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("2*(-3)", binOps, unOps);

        Assertions.assertEquals(ExpressionParser.parse(lexer).eval(""), -6);
    }

    @Test
    void test6() throws EvaluationException, ParserException, AssignmentParserException {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));
        binOps.put('-', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Sub.class));
        binOps.put('*', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Mul.class));
        binOps.put('/', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Div.class));
        unOps.put('-', new UnOperatorToken(3, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("x*(-y)", binOps, unOps);

        Assertions.assertEquals(ExpressionParser.parse(lexer).eval("x= 2; y    =   3"), -6);
    }

    @Test
    void test7() throws EvaluationException, ParserException, AssignmentParserException {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));
        binOps.put('-', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Sub.class));
        binOps.put('*', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Mul.class));
        binOps.put('/', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Div.class));
        unOps.put('-', new UnOperatorToken(3, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("2*-3", binOps, unOps);

        Assertions.assertEquals(ExpressionParser.parse(lexer).eval(""), -6);
    }

    @Test
    void test8() throws EvaluationException, ParserException, AssignmentParserException {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));
        binOps.put('-', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Sub.class));
        binOps.put('*', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Mul.class));
        binOps.put('/', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Div.class));
        unOps.put('-', new UnOperatorToken(3, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("x*-y", binOps, unOps);

        Assertions.assertEquals(ExpressionParser.parse(lexer).eval("x= 2; y    =   3"), -6);
    }

    @Test
    void test9() throws EvaluationException, ParserException, AssignmentParserException {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));
        binOps.put('-', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Sub.class));
        binOps.put('*', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Mul.class));
        binOps.put('/', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Div.class));
        unOps.put('-', new UnOperatorToken(3, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("2 * 2 + 2", binOps, unOps);

        Assertions.assertEquals(ExpressionParser.parse(lexer).eval(""), 6);
    }

    @Test
    void testError() throws EvaluationException, ParserException {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));
        binOps.put('-', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Sub.class));
        binOps.put('*', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Mul.class));
        binOps.put('/', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Div.class));
        unOps.put('-', new UnOperatorToken(3, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("(x*y", binOps, unOps);

        Assertions.assertThrows(
                ParserException.class,
                () -> {
                    ExpressionParser.parse(lexer);
                });
    }

    @Test
    void testError2() throws EvaluationException, ParserException {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));
        binOps.put('-', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Sub.class));
        binOps.put('*', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Mul.class));
        binOps.put('/', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Div.class));
        unOps.put('-', new UnOperatorToken(3, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("22 22 + 33", binOps, unOps);

        Assertions.assertThrows(
                ParserException.class,
                () -> {
                    ExpressionParser.parse(lexer);
                });
    }

    @Test
    void testError3() throws EvaluationException, ParserException {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));
        binOps.put('-', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Sub.class));
        binOps.put('*', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Mul.class));
        binOps.put('/', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Div.class));
        unOps.put('-', new UnOperatorToken(3, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("+22", binOps, unOps);

        Assertions.assertThrows(
                InputMismatchException.class,
                () -> {
                    ExpressionParser.parse(lexer);
                });
    }

    @Test
    void testError4() throws EvaluationException, ParserException {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));
        binOps.put('-', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Sub.class));
        binOps.put('*', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Mul.class));
        binOps.put('/', new BinOperatorToken(2, OperatorToken.Associativity.LEFT, Div.class));
        unOps.put('-', new UnOperatorToken(3, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("3+22)", binOps, unOps);

        Assertions.assertThrows(
                ParserException.class,
                () -> {
                    ExpressionParser.parse(lexer);
                });
    }
}
