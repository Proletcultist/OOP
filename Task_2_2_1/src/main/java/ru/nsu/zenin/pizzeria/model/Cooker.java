package ru.nsu.zenin.pizzeria.model;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Cooker extends PizzeriaWorker {

    private final long timeToCook;

    public void run() {
        boolean stopToWork = false;
        while (true) {
            try {
                Order ord = pizzeria.getPendingOrders().poll();
                if (ord == null && stopToWork) {
                    return;
                } else if (ord == null) {
                    ord = pizzeria.getPendingOrders().take();
                }

                ord.setStatus(Order.OrderStatus.COOKING);

                TimeUnit.MILLISECONDS.sleep(timeToCook * pizzeria.getVirtualHourValue() / 60);

                ord.setStatus(Order.OrderStatus.WAITING_FOR_DELIVERER);
                pizzeria.getWarehouse().put(ord);
            } catch (InterruptedException e) {
                stopToWork = true;
            }
        }
    }
}
