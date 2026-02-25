package ru.nsu.zenin.pizzeria.model;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import ru.nsu.zenin.collection.BlockingCircularBuffer;
import ru.nsu.zenin.collection.BlockingLinkedList;
import ru.nsu.zenin.collection.BlockingQueue;

public class Pizzeria {
    private final List<PizzeriaWorker> workers;

    @Getter(AccessLevel.PACKAGE)
    private final BlockingQueue<Order> warehouse;

    @Getter(AccessLevel.PACKAGE)
    private final BlockingQueue<Order> orders;

    @Getter(AccessLevel.PACKAGE)
    private final long virtualHourValue;

    public Pizzeria(long virtualHourValue, int warehouseCapacity, List<PizzeriaWorker> workers) {
        this.workers = workers;
        this.virtualHourValue = virtualHourValue;
        this.warehouse = new BlockingCircularBuffer<Order>(warehouseCapacity);
        this.orders = new BlockingLinkedList<Order>();
    }

    public void employ(PizzeriaWorker worker) {
        worker.setPizzeria(this);
        workers.add(worker);
    }
}
