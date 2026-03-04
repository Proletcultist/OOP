package ru.nsu.zenin.pizzeria.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Deliverer extends PizzeriaWorker {
    private static long MIN_DELIVERY_TIME = 15;
    private static long MAX_DELIVERY_TIME = 45;

    private final int trunkCapacity;

    public void run() {
        boolean stopToWork = false;
        while (true) {
            try {
                List<Order> taken = new ArrayList<Order>();

                Order fst = pizzeria.getWarehouse().poll();
                if (fst == null && stopToWork) {
                    return;
                } else if (fst == null) {
                    fst = pizzeria.getWarehouse().take();
                }
                fst.setStatus(Order.OrderStatus.IN_DELIVERY);
                taken.add(fst);

                while (taken.size() < trunkCapacity) {
                    Order ord = pizzeria.getWarehouse().poll();
                    if (ord == null) {
                        break;
                    } else {
                        ord.setStatus(Order.OrderStatus.IN_DELIVERY);
                        taken.add(ord);
                    }
                }

                TimeUnit.MILLISECONDS.sleep(
                        (long) taken.size()
                                * ThreadLocalRandom.current()
                                        .nextLong(MIN_DELIVERY_TIME, MAX_DELIVERY_TIME)
                                * pizzeria.getVirtualHourValue()
                                / 60);

                for (Order ord : taken) {
                    ord.setStatus(Order.OrderStatus.DELIVERED);
                }

            } catch (InterruptedException e) {
                stopToWork = true;
            }
        }
    }
}
