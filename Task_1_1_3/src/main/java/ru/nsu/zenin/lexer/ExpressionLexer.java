package ru.nsu.zenin.lexer;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.nsu.zenin.lexer.token.BinOperatorToken;
import ru.nsu.zenin.lexer.token.BracketToken;
import ru.nsu.zenin.lexer.token.NumberToken;
import ru.nsu.zenin.lexer.token.UnOperatorToken;
import ru.nsu.zenin.lexer.token.VariableToken;

public class ExpressionLexer {

    private static final String BLANK_REGEX = "\\s+";
    private static final String INTEGER_REGEX = "\\d+";
    private static final String BRACKET_OPEN_REGEX = "\\(";
    private static final String BRACKET_CLOSE_REGEX = "\\)";

    // Variable or operator
    private static final String IDENTIFIER_REGEX = "[^\\d\\s()][^\\s()]*";

    private static final Pattern TOKEN_PATTERN =
            Pattern.compile(
                    "("
                            + BLANK_REGEX
                            + ")"
                            + "|"
                            + "("
                            + INTEGER_REGEX
                            + ")"
                            + "|"
                            + "("
                            + BRACKET_OPEN_REGEX
                            + ")"
                            + "|"
                            + "("
                            + BRACKET_CLOSE_REGEX
                            + ")"
                            + "|"
                            + "("
                            + IDENTIFIER_REGEX
                            + ")");

    private final Map<Character, BinOperatorToken> binOps;
    private final Map<Character, UnOperatorToken> unOps;

    private final String input;
    private int position = 0;

    private TokenType lastTokenType = null;

    public ExpressionLexer(
            String str,
            Map<Character, BinOperatorToken> binOps,
            Map<Character, UnOperatorToken> unOps) {
        this.binOps = binOps;
        this.unOps = unOps;
        this.input = str;

        for (char c : binOps.keySet()) {
            if (c == '(' || c == ')' || Character.isDigit(c) || Character.isWhitespace(c)) {
                throw new InputMismatchException("Invalid operator name");
            }
        }
        for (char c : unOps.keySet()) {
            if (c == '(' || c == ')' || Character.isDigit(c) || Character.isWhitespace(c)) {
                throw new InputMismatchException("Invalid operator name");
            }
        }
    }

    public boolean hasNextToken() {
        skipBlank();
        return position < input.length();
    }

    public TokenType nextTokenType() {
        skipBlank();

        if (position >= input.length()) {
            throw new NoSuchElementException("String is all-read");
        }

        Matcher matcher = TOKEN_PATTERN.matcher(input.substring(position));

        if (matcher.find()) {

            if (matcher.group(2) != null) {
                return TokenType.NUMBER;
            } else if (matcher.group(3) != null) {
                return TokenType.OPEN_PARENTHESIS;
            } else if (matcher.group(4) != null) {
                return TokenType.CLOSE_PARENTHESIS;
            } else if (matcher.group(5) != null) {
                if ((lastTokenType == null
                                || lastTokenType != TokenType.CLOSE_PARENTHESIS
                                        && lastTokenType != TokenType.NUMBER
                                        && lastTokenType != TokenType.VARIABLE)
                        && unOps.containsKey(matcher.group(5).charAt(0))) {
                    return TokenType.UN_OPERATOR;
                } else if (binOps.containsKey(matcher.group(5).charAt(0))) {
                    return TokenType.BIN_OPERATOR;
                } else {
                    return TokenType.VARIABLE;
                }
            }
        }

        throw new InputMismatchException("Unknown token found");
    }

    public BinOperatorToken nextBinOperator() {
        skipBlank();

        if (position >= input.length()) {
            throw new NoSuchElementException("String is all-read");
        }

        Matcher matcher = TOKEN_PATTERN.matcher(input.substring(position));

        if (!matcher.find()
                || matcher.group(5) == null
                || !(lastTokenType != null
                        && (lastTokenType == TokenType.CLOSE_PARENTHESIS
                                || lastTokenType == TokenType.NUMBER
                                || lastTokenType == TokenType.VARIABLE)
                        && binOps.containsKey(matcher.group(5).charAt(0)))) {
            throw new InputMismatchException();
        }

        position++;
        lastTokenType = TokenType.BIN_OPERATOR;

        return binOps.get(matcher.group(5).charAt(0));
    }

    public UnOperatorToken nextUnOperator() {
        skipBlank();

        if (position >= input.length()) {
            throw new NoSuchElementException("String is all-read");
        }

        Matcher matcher = TOKEN_PATTERN.matcher(input.substring(position));

        if (!matcher.find()
                || matcher.group(5) == null
                || !((lastTokenType == null
                                || lastTokenType != TokenType.CLOSE_PARENTHESIS
                                        && lastTokenType != TokenType.NUMBER
                                        && lastTokenType != TokenType.VARIABLE)
                        && unOps.containsKey(matcher.group(5).charAt(0)))) {
            throw new InputMismatchException();
        }

        position++;
        lastTokenType = TokenType.UN_OPERATOR;

        return unOps.get(matcher.group(5).charAt(0));
    }

    public NumberToken nextNumberToken() {
        skipBlank();

        if (position >= input.length()) {
            throw new NoSuchElementException("String is all-read");
        }

        Matcher matcher = TOKEN_PATTERN.matcher(input.substring(position));

        if (!matcher.find() || matcher.group(2) == null) {
            throw new InputMismatchException();
        }

        position += matcher.group(2).length();
        lastTokenType = TokenType.NUMBER;

        return new NumberToken(Integer.valueOf(matcher.group(2)));
    }

    public VariableToken nextVariableToken() {
        skipBlank();

        if (position >= input.length()) {
            throw new NoSuchElementException("String is all-read");
        }

        Matcher matcher = TOKEN_PATTERN.matcher(input.substring(position));

        if (!matcher.find() || matcher.group(5) == null) {
            throw new InputMismatchException();
        }

        // Check for operators inside group
        int realSize = 0;
        while (realSize < matcher.group(5).length()
                && !unOps.containsKey(matcher.group(5).charAt(realSize))
                && !binOps.containsKey(matcher.group(5).charAt(realSize))) {
            realSize++;
        }

        position += realSize;
        lastTokenType = TokenType.VARIABLE;

        return new VariableToken(matcher.group(5).substring(0, realSize));
    }

    public BracketToken nextOpenParenthesis() {
        skipBlank();

        if (position >= input.length()) {
            throw new NoSuchElementException("String is all-read");
        }

        Matcher matcher = TOKEN_PATTERN.matcher(input.substring(position));

        if (!matcher.find() || matcher.group(3) == null) {
            throw new InputMismatchException();
        }

        position += matcher.group(3).length();
        lastTokenType = TokenType.OPEN_PARENTHESIS;

        return new BracketToken(true);
    }

    public BracketToken nextCloseParenthesis() {
        skipBlank();

        if (position >= input.length()) {
            throw new NoSuchElementException("String is all-read");
        }

        Matcher matcher = TOKEN_PATTERN.matcher(input.substring(position));

        if (!matcher.find() || matcher.group(4) == null) {
            throw new InputMismatchException();
        }

        position += matcher.group(4).length();
        lastTokenType = TokenType.CLOSE_PARENTHESIS;

        return new BracketToken(false);
    }

    private void skipBlank() {
        if (position >= input.length()) {
            return;
        }

        Matcher matcher = TOKEN_PATTERN.matcher(input.substring(position));

        if (matcher.find() && matcher.group(1) != null) {
            position += matcher.group(1).length();
        }
    }

    public enum TokenType {
        BIN_OPERATOR,
        UN_OPERATOR,
        NUMBER,
        VARIABLE,
        OPEN_PARENTHESIS,
        CLOSE_PARENTHESIS
    }
}
