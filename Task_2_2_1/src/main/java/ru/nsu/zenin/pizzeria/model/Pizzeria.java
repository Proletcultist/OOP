package ru.nsu.zenin.pizzeria.model;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import lombok.AccessLevel;
import lombok.Getter;
import ru.nsu.zenin.collections.SyncCircularBuffer;

public class Pizzeria {
    private final List<PizzeriaWorker> workers;

    @Getter(AccessLevel.PACKAGE)
    private final SyncCircularBuffer<Integer> warehouse;

    @Getter(AccessLevel.PACKAGE)
    private final Queue<Integer> orders;

    @Getter(AccessLevel.PACKAGE)
    private final long virtualHourValue;

    public Pizzeria(long virtualHourValue, int warehouseCapacity, List<PizzeriaWorker> workers) {
        this.workers = workers;
        this.virtualHourValue = virtualHourValue;
        this.warehouse = new SyncCircularBuffer<Integer>(warehouseCapacity);
        this.orders = new ArrayDeque<Integer>();
    }

    public void employ(PizzeriaWorker worker) {
        worker.setPizzeria(this);
        workers.add(worker);
    }

    @Override
    public String toString() {
        return "Workers: " + workers.toString();
    }
}
