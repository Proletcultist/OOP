package ru.nsu.zenin.pizzeria.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import ru.nsu.zenin.pizzeria.exception.NoSuchOrderException;

public class Deliverer extends PizzeriaWorker {
    private static long MIN_DELIVERY_TIME = 15;
    private static long MAX_DELIVERY_TIME = 45;

    private final int trunkCapacity;

    public Deliverer(int trunkCapacity) {
        if (trunkCapacity < 0) {
            throw new IllegalArgumentException("Trunk capacity cannot be negative or zero");
        }
        this.trunkCapacity = trunkCapacity;
    }

    public void run() {
        while (true) {
            try {
                List<Integer> taken = new ArrayList<Integer>();

                int fst = pizzeria.getWarehouse().take();
                pizzeria.setOrderStatus(fst, Order.OrderStatus.IN_DELIVERY);
                taken.add(fst);

                while (taken.size() < trunkCapacity) {
                    Integer ord = pizzeria.getWarehouse().poll();
                    if (ord == null) {
                        break;
                    } else {
                        pizzeria.setOrderStatus(ord, Order.OrderStatus.IN_DELIVERY);
                        taken.add(ord);
                    }
                }

                for (int ord : taken) {
                    TimeUnit.MILLISECONDS.sleep(
                            ThreadLocalRandom.current()
                                            .nextLong(MIN_DELIVERY_TIME, MAX_DELIVERY_TIME)
                                    * pizzeria.getVirtualHourValue()
                                    / 60);
                    pizzeria.setOrderStatus(ord, Order.OrderStatus.DELIVERED);
                }

            } catch (InterruptedException e) {
                return;
            } catch (NoSuchOrderException e) {
                throw new RuntimeException("Unexpected exception occured", e);
            }
        }
    }
}
