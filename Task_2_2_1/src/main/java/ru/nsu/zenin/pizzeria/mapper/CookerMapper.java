package ru.nsu.zenin.pizzeria.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.nsu.zenin.pizzeria.dto.CookerDTO;
import ru.nsu.zenin.pizzeria.model.Cooker;

@Mapper
public interface CookerMapper {
    CookerMapper INSTANCE = Mappers.getMapper(CookerMapper.class);

    public Cooker fromCookerDTO(CookerDTO dto);
}
