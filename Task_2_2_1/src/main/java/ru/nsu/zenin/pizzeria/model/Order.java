package ru.nsu.zenin.pizzeria.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Order {

    private final int id;
    private final Pizza pizza;

    @Getter(AccessLevel.PACKAGE)
    private OrderStatus status = OrderStatus.WAITING_FOR_COOKER;

    void setStatus(Order.OrderStatus status) {
        this.status = status;
        System.err.println("Order " + this.id + " changed status to: " + this.status);
    }

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
