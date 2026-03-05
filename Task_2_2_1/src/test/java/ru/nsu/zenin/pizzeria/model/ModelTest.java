package ru.nsu.zenin.pizzeria.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.pizzeria.exception.IllegalPizzeriaStateException;
import ru.nsu.zenin.pizzeria.exception.NoSuchOrderException;

class ModelTest {

    @Test
    void pizzeriaTest() throws Exception {
        List<PizzeriaWorker> workers = new ArrayList<PizzeriaWorker>();
        workers.add(new Cooker(0));
        workers.add(new Deliverer(10));

        Pizzeria pizzeria = new Pizzeria(0, 100, workers);

        pizzeria.start();

        int id = pizzeria.makeOrder(Pizza.PEPERONI);

        while (true) {
            if (pizzeria.getOrderStatus(id) == Order.OrderStatus.DELIVERED) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }

        Assertions.assertEquals(pizzeria.getOrderStatus(id), Order.OrderStatus.DELIVERED);
        pizzeria.stop();
    }

    @Test
    void stopStoppedPizzeriaTest() throws Exception {
        List<PizzeriaWorker> workers = new ArrayList<PizzeriaWorker>();

        Pizzeria pizzeria = new Pizzeria(0, 100, workers);

        Assertions.assertThrows(
                IllegalPizzeriaStateException.class,
                () -> {
                    pizzeria.stop();
                });
    }

    @Test
    void startStartedPizzeriaTest() throws Exception {
        List<PizzeriaWorker> workers = new ArrayList<PizzeriaWorker>();

        Pizzeria pizzeria = new Pizzeria(0, 100, workers);

        pizzeria.start();

        Assertions.assertThrows(
                IllegalPizzeriaStateException.class,
                () -> {
                    pizzeria.start();
                });

        pizzeria.stop();
    }

    @Test
    void noSuchOrderTest() throws Exception {
        List<PizzeriaWorker> workers = new ArrayList<PizzeriaWorker>();

        Pizzeria pizzeria = new Pizzeria(0, 100, workers);

        Assertions.assertThrows(
                NoSuchOrderException.class,
                () -> {
                    pizzeria.getOrderStatus(1337);
                });
    }
}
