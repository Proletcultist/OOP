package ru.nsu.zenin.pizzeria.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.nsu.zenin.pizzeria.dto.PizzeriaDTO;
import ru.nsu.zenin.pizzeria.model.Pizzeria;
import ru.nsu.zenin.pizzeria.model.PizzeriaWorker;

@Mapper
public abstract class PizzeriaMapper {
    public static PizzeriaMapper INSTANCE = Mappers.getMapper(PizzeriaMapper.class);

    @Mapping(target = "workers", source = ".", qualifiedByName = "getWorkers")
    public abstract Pizzeria fromPizzeriaDTO(PizzeriaDTO dto);

    @Named("getWorkers")
    protected List<PizzeriaWorker> employAllWorkers(PizzeriaDTO dto) {
        return Stream.concat(
                        Arrays.stream(dto.deliverers())
                                .map(d -> DelivererMapper.INSTANCE.fromDelivererDTO(d)),
                        Arrays.stream(dto.cookers())
                                .map(c -> CookerMapper.INSTANCE.fromCookerDTO(c)))
                .collect(Collectors.toList());
    }
}
