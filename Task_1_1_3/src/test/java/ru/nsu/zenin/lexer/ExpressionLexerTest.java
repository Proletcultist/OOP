package ru.nsu.zenin.lexer;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.expression.Add;
import ru.nsu.zenin.expression.UnSub;
import ru.nsu.zenin.lexer.token.BinOperatorToken;
import ru.nsu.zenin.lexer.token.OperatorToken;
import ru.nsu.zenin.lexer.token.UnOperatorToken;

class ExpressionLexerTest {

    @Test
    void test() {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        ExpressionLexer lexer = new ExpressionLexer("  12321    ", binOps, unOps);

        Assertions.assertTrue(lexer.hasNextToken());

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.NUMBER);
        Assertions.assertEquals(lexer.nextNumberToken().getValue(), 12321);

        Assertions.assertFalse(lexer.hasNextToken());
    }

    @Test
    void test2() {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));

        ExpressionLexer lexer = new ExpressionLexer("  12321+ 2", binOps, unOps);

        Assertions.assertTrue(lexer.hasNextToken());

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.NUMBER);
        Assertions.assertEquals(lexer.nextNumberToken().getValue(), 12321);

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.BIN_OPERATOR);
        Assertions.assertEquals(lexer.nextBinOperator(), binOps.get('+'));

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.NUMBER);
        Assertions.assertEquals(lexer.nextNumberToken().getValue(), 2);

        Assertions.assertFalse(lexer.hasNextToken());
    }

    @Test
    void test3() {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        binOps.put('+', new BinOperatorToken(1, OperatorToken.Associativity.LEFT, Add.class));

        ExpressionLexer lexer = new ExpressionLexer("(12321+x)", binOps, unOps);

        Assertions.assertTrue(lexer.hasNextToken());

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.OPEN_PARENTHESIS);
        lexer.nextOpenParenthesis();

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.NUMBER);
        Assertions.assertEquals(lexer.nextNumberToken().getValue(), 12321);

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.BIN_OPERATOR);
        Assertions.assertEquals(lexer.nextBinOperator(), binOps.get('+'));

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.VARIABLE);
        Assertions.assertEquals(lexer.nextVariableToken().getName(), "x");

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.CLOSE_PARENTHESIS);
        lexer.nextCloseParenthesis();

        Assertions.assertFalse(lexer.hasNextToken());
    }

    @Test
    void test4() {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        unOps.put('-', new UnOperatorToken(1, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("-123", binOps, unOps);

        Assertions.assertTrue(lexer.hasNextToken());

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.UN_OPERATOR);
        Assertions.assertEquals(lexer.nextUnOperator(), unOps.get('-'));

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.NUMBER);
        Assertions.assertEquals(lexer.nextNumberToken().getValue(), 123);

        Assertions.assertFalse(lexer.hasNextToken());
    }

    @Test
    void test5() {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        unOps.put('-', new UnOperatorToken(1, OperatorToken.Associativity.LEFT, UnSub.class));

        ExpressionLexer lexer = new ExpressionLexer("(-y)", binOps, unOps);

        Assertions.assertTrue(lexer.hasNextToken());

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.OPEN_PARENTHESIS);
        lexer.nextOpenParenthesis();

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.UN_OPERATOR);
        Assertions.assertEquals(lexer.nextUnOperator(), unOps.get('-'));

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.VARIABLE);
        Assertions.assertEquals(lexer.nextVariableToken().getName(), "y");

        Assertions.assertEquals(lexer.nextTokenType(), ExpressionLexer.TokenType.CLOSE_PARENTHESIS);
        lexer.nextCloseParenthesis();

        Assertions.assertFalse(lexer.hasNextToken());
    }

    @Test
    void test6() {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        ExpressionLexer lexer = new ExpressionLexer("", binOps, unOps);

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> {
                    lexer.nextTokenType();
                });

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> {
                    lexer.nextBinOperator();
                });

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> {
                    lexer.nextNumberToken();
                });

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> {
                    lexer.nextOpenParenthesis();
                });

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> {
                    lexer.nextUnOperator();
                });

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> {
                    lexer.nextVariableToken();
                });

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> {
                    lexer.nextCloseParenthesis();
                });
    }

    @Test
    void test7() {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        ExpressionLexer lexer = new ExpressionLexer("boba", binOps, unOps);

        Assertions.assertThrows(
                InputMismatchException.class,
                () -> {
                    lexer.nextNumberToken();
                });

        Assertions.assertThrows(
                InputMismatchException.class,
                () -> {
                    lexer.nextUnOperator();
                });

        Assertions.assertThrows(
                InputMismatchException.class,
                () -> {
                    lexer.nextBinOperator();
                });

        Assertions.assertThrows(
                InputMismatchException.class,
                () -> {
                    lexer.nextOpenParenthesis();
                });

        Assertions.assertThrows(
                InputMismatchException.class,
                () -> {
                    lexer.nextCloseParenthesis();
                });
    }

    @Test
    void test8() {
        HashMap<Character, BinOperatorToken> binOps = new HashMap<Character, BinOperatorToken>();
        HashMap<Character, UnOperatorToken> unOps = new HashMap<Character, UnOperatorToken>();

        ExpressionLexer lexer = new ExpressionLexer("45", binOps, unOps);

        Assertions.assertThrows(
                InputMismatchException.class,
                () -> {
                    lexer.nextVariableToken();
                });
    }
}
