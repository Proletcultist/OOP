package ru.nsu.zenin.pizzeria.exception;

public class NoSuchOrderException extends Exception {
    public NoSuchOrderException(String msg) {
        super(msg);
    }
}
