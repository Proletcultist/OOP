package ru.nsu.zenin.pizzeria.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.nsu.zenin.pizzeria.dto.DelivererDTO;
import ru.nsu.zenin.pizzeria.model.Deliverer;

@Mapper
public interface DelivererMapper {
    DelivererMapper INSTANCE = Mappers.getMapper(DelivererMapper.class);

    Deliverer fromDelivererDTO(DelivererDTO dto);
}
