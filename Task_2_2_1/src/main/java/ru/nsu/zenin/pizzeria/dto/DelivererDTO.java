package ru.nsu.zenin.pizzeria.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DelivererDTO(@JsonProperty(required = true) int trunkCapacity) {}
