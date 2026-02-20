package ru.nsu.zenin.pizzeria.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PizzeriaDTO(
        @JsonProperty(required = true) CookerDTO[] cookers,
        @JsonProperty(required = true) DelivererDTO[] deliverers,
        @JsonProperty(required = true) long virtualHourValue,
        @JsonProperty(required = true) int warehouseCapacity) {}
