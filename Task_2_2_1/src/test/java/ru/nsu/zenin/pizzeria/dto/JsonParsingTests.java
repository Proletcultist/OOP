package ru.nsu.zenin.pizzeria.dto;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class JsonParsingTests {
    @Test
    void testParse() throws Exception {
        try (InputStream is =
                Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("correctConfig.json")) {
            ObjectMapper mapper = new ObjectMapper();
            PizzeriaDTO pizzeriaConfig = mapper.readValue(is, PizzeriaDTO.class);

            Assertions.assertEquals(pizzeriaConfig.virtualHourValue(), 20);
            Assertions.assertEquals(pizzeriaConfig.warehouseCapacity(), 100);

            Assertions.assertEquals(pizzeriaConfig.cookers().length, 1);
            Assertions.assertEquals(pizzeriaConfig.deliverers().length, 1);

            Assertions.assertEquals(pizzeriaConfig.cookers()[0].timeToCook(), 1);
            Assertions.assertEquals(pizzeriaConfig.deliverers()[0].trunkCapacity(), 2);
        }
    }

    @Test
    void testParse2() throws Exception {
        try (InputStream is =
                Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("emptyListsConfig.json")) {
            ObjectMapper mapper = new ObjectMapper();
            PizzeriaDTO pizzeriaConfig = mapper.readValue(is, PizzeriaDTO.class);

            Assertions.assertEquals(pizzeriaConfig.virtualHourValue(), 20);
            Assertions.assertEquals(pizzeriaConfig.warehouseCapacity(), 100);

            Assertions.assertEquals(pizzeriaConfig.cookers().length, 0);
            Assertions.assertEquals(pizzeriaConfig.deliverers().length, 0);
        }
    }

    @Test
    void testInvalid() throws Exception {
        try (InputStream is =
                Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("invalidConfig.json")) {

            Assertions.assertThrows(
                    JsonMappingException.class,
                    () -> {
                        ObjectMapper mapper = new ObjectMapper();
                        PizzeriaDTO pizzeriaConfig = mapper.readValue(is, PizzeriaDTO.class);
                    });
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalidConfig2", "invalidConfig3", "invalidConfig4", "invalidConfig5"})
    void testInvalid(String filename) throws Exception {
        try (InputStream is =
                Thread.currentThread().getContextClassLoader().getResourceAsStream(filename)) {

            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> {
                        ObjectMapper mapper = new ObjectMapper();
                        PizzeriaDTO pizzeriaConfig = mapper.readValue(is, PizzeriaDTO.class);
                    });
        }
    }
}
