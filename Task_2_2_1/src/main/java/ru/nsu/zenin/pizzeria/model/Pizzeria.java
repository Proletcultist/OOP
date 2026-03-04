package ru.nsu.zenin.pizzeria.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import ru.nsu.zenin.collection.BlockingCircularBuffer;
import ru.nsu.zenin.collection.BlockingLinkedList;
import ru.nsu.zenin.collection.BlockingQueue;
import ru.nsu.zenin.pizzeria.exception.IllegalPizzeriaStateException;
import ru.nsu.zenin.pizzeria.exception.NoSuchOrderException;

public class Pizzeria {
    private final List<PizzeriaWorker> workers;
    private ThreadGroup workerThreads = null;

    @Getter(AccessLevel.PACKAGE)
    private final BlockingQueue<Order> warehouse;

    @Getter(AccessLevel.PACKAGE)
    private final BlockingQueue<Order> pendingOrders = new BlockingLinkedList<Order>();

    private final Map<Integer, Order> orderById = new HashMap<Integer, Order>();

    private int nextOrderId = 0;

    @Getter(AccessLevel.PACKAGE)
    private final long virtualHourValue;

    public Pizzeria(long virtualHourValue, int warehouseCapacity, List<PizzeriaWorker> workers) {
        this.workers = workers;
        this.virtualHourValue = virtualHourValue;
        this.warehouse = new BlockingCircularBuffer<Order>(warehouseCapacity);
    }

    public synchronized void start() throws IllegalPizzeriaStateException {
        if (workerThreads != null) {
            throw new IllegalPizzeriaStateException("Starting already started pizzeria");
        }
        nextOrderId = 0;
        workerThreads = new ThreadGroup("workers");
        for (PizzeriaWorker worker : workers) {
            Thread thread = new Thread(workerThreads, worker);
            thread.start();
        }
    }

    public synchronized void stop() throws IllegalPizzeriaStateException {
        if (workerThreads == null) {
            throw new IllegalPizzeriaStateException("Stopping already stopped pizzeria");
        }
        try {
            pendingOrders.blockUntilEmpty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            workerThreads.interrupt();
            workerThreads = null;
        }
    }

    public synchronized int makeOrder(Pizza pizza) throws IllegalPizzeriaStateException {
        if (workerThreads == null) {
            throw new IllegalPizzeriaStateException("Cannot make order in closed pizzeria");
        }
        try {
            Order neww = new Order(nextOrderId, pizza);
            orderById.put(nextOrderId, neww);
            pendingOrders.put(neww);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        nextOrderId += 1;
        return nextOrderId - 1;
    }

    public Order.OrderStatus getOrderStatus(int id) throws NoSuchOrderException {
        if (!orderById.containsKey(id)) {
            throw new NoSuchOrderException("No order with id " + id + " in pizzeria");
        }
        return orderById.get(id).getStatus();
    }

    public void employ(PizzeriaWorker worker) {
        worker.setPizzeria(this);
        workers.add(worker);
    }
}
