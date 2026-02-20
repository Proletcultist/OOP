package ru.nsu.zenin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nsu.zenin.pizzeria.dto.PizzeriaDTO;
import ru.nsu.zenin.pizzeria.mapper.PizzeriaMapper;
import ru.nsu.zenin.pizzeria.model.Pizzeria;

public class Main {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json =
                """
                      {
                          \"cookers\" : [{\"timeToCook\": 20}],
                          \"deliverers\" : [{\"trunkCapacity\" : 10}],
                          \"virtualHourValue\" : 123,
                          \"warehouseCapacity\": 20
                      }
                      """;

        PizzeriaDTO pizzeriaConfig;
        try {
            pizzeriaConfig = objectMapper.readValue(json, PizzeriaDTO.class);
            Pizzeria pizzeria = PizzeriaMapper.INSTANCE.fromPizzeriaDTO(pizzeriaConfig);

            System.out.println(pizzeria.toString());
        } catch (JsonProcessingException e) {
            System.out.println("Failed to read config:\n\t" + e.toString());
        }
    }
}
