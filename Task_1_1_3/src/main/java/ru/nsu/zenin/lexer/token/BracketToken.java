package ru.nsu.zenin.lexer.token;

public class BracketToken {
    private final boolean isOpen;

    public BracketToken(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public boolean isOpen() {
        return this.isOpen;
    }
}
