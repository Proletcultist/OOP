package ru.nsu.zenin.parser;

import java.util.EmptyStackException;
import java.util.Stack;
import ru.nsu.zenin.expression.Expression;
import ru.nsu.zenin.lexer.ExpressionLexer;
import ru.nsu.zenin.lexer.token.BinOperatorToken;
import ru.nsu.zenin.lexer.token.OperatorToken;
import ru.nsu.zenin.lexer.token.UnOperatorToken;
import ru.nsu.zenin.parser.exception.ParserException;

public class ExpressionParser {

    private ExpressionParser() {}

    public static Expression parse(ExpressionLexer lexer) throws ParserException {
        Stack<Expression> expressionsStack = new Stack<Expression>();
        Stack<OperatorToken> operatorsStack = new Stack<OperatorToken>();

        while (lexer.hasNextToken()) {
            ExpressionLexer.TokenType nextTokenType = lexer.nextTokenType();

            switch (nextTokenType) {
                case BIN_OPERATOR:
                    processOperator(lexer.nextBinOperator(), expressionsStack, operatorsStack);
                    break;
                case UN_OPERATOR:
                    processOperator(lexer.nextUnOperator(), expressionsStack, operatorsStack);
                    break;
                case NUMBER:
                    expressionsStack.push(lexer.nextNumberToken().asNumberExpr());
                    break;
                case VARIABLE:
                    expressionsStack.push(lexer.nextVariableToken().asVariableExpr());
                    break;
                case OPEN_PARENTHESIS:
                    lexer.nextOpenParenthesis();
                    operatorsStack.push(new OpenParenthesis());
                    break;
                case CLOSE_PARENTHESIS:
                    processCloseParenthesis(lexer, expressionsStack, operatorsStack);
                    break;
            }
        }

        while (!operatorsStack.empty()) {
            OperatorToken op = operatorsStack.pop();

            if (op instanceof OpenParenthesis) {
                throw new ParserException("Missing matching bracket");
            }

            applyOperator(op, expressionsStack);
        }

        if (expressionsStack.size() != 1) {
            throw new ParserException("Invalid expression");
        }

        return expressionsStack.pop();
    }

    private static void processOperator(
            OperatorToken op,
            Stack<Expression> expressionsStack,
            Stack<OperatorToken> operatorsStack)
            throws ParserException {

        while (true) {
            if (operatorsStack.empty()) {
                break;
            }

            OperatorToken topOp = operatorsStack.peek();

            if (topOp instanceof OpenParenthesis) {
                break;
            }

            if (topOp.getPriority() < op.getPriority()
                    || topOp.getPriority() == op.getPriority()
                            && op.getAssociativity() != OperatorToken.Associativity.LEFT) {
                break;
            }

            applyOperator(operatorsStack.pop(), expressionsStack);
        }

        operatorsStack.push(op);
    }

    private static void applyOperator(OperatorToken op, Stack<Expression> expressionsStack)
            throws ParserException {
        try {
            if (op instanceof BinOperatorToken) {
                Expression rightOperand = expressionsStack.pop();
                Expression leftOperand = expressionsStack.pop();

                expressionsStack.push(
                        ((BinOperatorToken) op).getExpression(leftOperand, rightOperand));
            } else if (op instanceof UnOperatorToken) {
                Expression operand = expressionsStack.pop();

                expressionsStack.push(((UnOperatorToken) op).getExpression(operand));
            } else {
                throw new RuntimeException("Unexpected OperatorToken subclass on operators stack");
            }
        } catch (EmptyStackException e) {
            throw new ParserException("Invalid expression");
        }
    }

    private static void processCloseParenthesis(
            ExpressionLexer lexer,
            Stack<Expression> expressionsStack,
            Stack<OperatorToken> operatorsStack)
            throws ParserException {
        lexer.nextCloseParenthesis();

        while (true) {
            if (operatorsStack.empty()) {
                throw new ParserException("Missing matching bracket");
            }

            OperatorToken topOp = operatorsStack.pop();

            if (topOp instanceof OpenParenthesis) {
                break;
            }

            applyOperator(topOp, expressionsStack);
        }
    }

    // Dummy operator for open parenthesis
    private static class OpenParenthesis extends OperatorToken {
        private OpenParenthesis() {
            super(0, OperatorToken.Associativity.LEFT);
        }
    }
}
