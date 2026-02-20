package ru.nsu.zenin.pizzeria.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Deliverer extends PizzeriaWorker {
    private final int trunkCapacity;

    public void run() {}

    public String toString() {
        return "Deliverer" + trunkCapacity;
    }
}
