package ru.nsu.zenin.pizzeria.model;

import lombok.AccessLevel;
import lombok.Setter;

public abstract class PizzeriaWorker implements Runnable {
    @Setter(AccessLevel.PACKAGE)
    protected Pizzeria pizzeria = null;
}
