package ru.nsu.zenin.pizzeria.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Cooker extends PizzeriaWorker {

    private final long timeToCook;

    public void run() {}

    @Override
    public String toString() {
        return "Cooker" + timeToCook;
    }
}
