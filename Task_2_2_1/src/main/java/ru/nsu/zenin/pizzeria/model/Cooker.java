package ru.nsu.zenin.pizzeria.model;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import ru.nsu.zenin.pizzeria.exception.NoSuchOrderException;

@RequiredArgsConstructor
public class Cooker extends PizzeriaWorker {

    private final long timeToCook;

    public void run() {
        while (true) {
            try {
                int ord = pizzeria.getPendingOrders().take();

                pizzeria.setOrderStatus(ord, Order.OrderStatus.COOKING);

                TimeUnit.MILLISECONDS.sleep(timeToCook * pizzeria.getVirtualHourValue() / 60);

                pizzeria.setOrderStatus(ord, Order.OrderStatus.WAITING_FOR_DELIVERER);
                pizzeria.getWarehouse().put(ord);
            } catch (InterruptedException e) {
                return;
            } catch (NoSuchOrderException e) {
                throw new RuntimeException("Unexpected exception occured", e);
            }
        }
    }
}
