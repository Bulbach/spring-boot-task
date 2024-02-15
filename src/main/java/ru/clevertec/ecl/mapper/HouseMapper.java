package ru.clevertec.ecl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.requestDto.RequestDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.entity.House;

@Component
@Mapper(componentModel = "spring")
public interface HouseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "residents", ignore = true)
    @Mapping(target = "owners", ignore = true)
    @Mapping(target = "houseHistories", ignore = true)
    House toModel(RequestDtoHouse requestDtoHouse);
    @Mapping(source = "createDate", target = "createDate", dateFormat = "yyyy-MM-dd HH:mm:ss.SSS")
    ResponseDtoHouse toDto(House house);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "residents", ignore = true)
    @Mapping(target = "owners", ignore = true)
    @Mapping(target = "houseHistories", ignore = true)
    void updateModel(RequestDtoHouse requestDtoHouse, @MappingTarget House house);
}
