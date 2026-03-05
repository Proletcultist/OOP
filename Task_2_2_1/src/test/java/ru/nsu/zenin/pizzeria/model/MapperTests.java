package ru.nsu.zenin.pizzeria.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.zenin.pizzeria.dto.CookerDTO;
import ru.nsu.zenin.pizzeria.dto.DelivererDTO;
import ru.nsu.zenin.pizzeria.dto.PizzeriaDTO;
import ru.nsu.zenin.pizzeria.mapper.CookerMapper;
import ru.nsu.zenin.pizzeria.mapper.DelivererMapper;
import ru.nsu.zenin.pizzeria.mapper.PizzeriaMapper;

class MapperTests {
    @Test
    void mapPizzeriaTest() {
        CookerDTO[] cookers = {new CookerDTO(22)};
        DelivererDTO[] deliverers = {new DelivererDTO(12)};
        PizzeriaDTO dto = new PizzeriaDTO(cookers, deliverers, 1, 2);
        Pizzeria pizzeria = PizzeriaMapper.INSTANCE.fromPizzeriaDTO(dto);

        Assertions.assertEquals(pizzeria.getVirtualHourValue(), 1);
    }

    @Test
    void mapInvalidPizzeriaTest() {
        CookerDTO[] cookers = {new CookerDTO(22)};
        DelivererDTO[] deliverers = {new DelivererDTO(12)};
        PizzeriaDTO dto = new PizzeriaDTO(cookers, deliverers, -1, 2);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Pizzeria pizzeria = PizzeriaMapper.INSTANCE.fromPizzeriaDTO(dto);
                });
    }

    @Test
    void mapInvalidPizzeriaTest2() {
        CookerDTO[] cookers = {new CookerDTO(22)};
        DelivererDTO[] deliverers = {new DelivererDTO(12)};
        PizzeriaDTO dto = new PizzeriaDTO(cookers, deliverers, 1, -2);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Pizzeria pizzeria = PizzeriaMapper.INSTANCE.fromPizzeriaDTO(dto);
                });
    }

    @Test
    void mapInvalidCookerTest() {
        CookerDTO dto = new CookerDTO(-22);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Cooker cooker = CookerMapper.INSTANCE.fromCookerDTO(dto);
                });
    }

    @Test
    void mapInvalidDelivererTest() {
        DelivererDTO dto = new DelivererDTO(-22);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Deliverer deliverer = DelivererMapper.INSTANCE.fromDelivererDTO(dto);
                });
    }
}
