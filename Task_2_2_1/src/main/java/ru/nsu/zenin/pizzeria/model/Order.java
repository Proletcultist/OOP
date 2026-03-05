package ru.nsu.zenin.pizzeria.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Order {

    private final int id;
    private final Pizza pizza;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private OrderStatus status = OrderStatus.WAITING_FOR_COOKER;

    @RequiredArgsConstructor
    public enum OrderStatus {
        WAITING_FOR_COOKER("Waiting for cooker"),
        COOKING("Cooking"),
        WAITING_FOR_DELIVERER("Waiting for deliverer"),
        IN_DELIVERY("In delivery"),
        DELIVERED("Delivered");

        private final String asString;

        @Override
        public String toString() {
            return asString;
        }
    }
}
