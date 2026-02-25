package ru.nsu.zenin.pizzeria.model;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Order {

    private final Pizza pizza;
    @Setter private OrderStatus status = OrderStatus.WAITING_FOR_COOKER;

    public enum OrderStatus {
        WAITING_FOR_COOKER,
        COOKING,
        WAITING_FOR_DELIVERER,
        IN_DELIVERING
    }
}
