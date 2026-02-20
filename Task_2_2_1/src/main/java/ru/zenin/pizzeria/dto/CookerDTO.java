package ru.nsu.zenin.pizzeria.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CookerDTO(@JsonProperty(required = true) long timeToCook) {}
