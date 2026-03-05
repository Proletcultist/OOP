package ru.nsu.zenin.pizzeria.exception;

public class IllegalPizzeriaStateException extends RuntimeException {
    public IllegalPizzeriaStateException(String msg) {
        super(msg);
    }
}
