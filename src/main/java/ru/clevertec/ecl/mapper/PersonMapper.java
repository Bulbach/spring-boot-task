package ru.clevertec.ecl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.requestDto.RequestDtoPerson;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.Person;

@Component
@Mapper(componentModel = "spring")
public interface PersonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownedHouses", ignore = true)
    @Mapping(target = "houseHistories", ignore = true)
    @Mapping(source = "isOwner", target = "owner")
    Person toModel(RequestDtoPerson requestDtoPerson);

    ResponseDtoPerson toDto(Person person);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownedHouses", ignore = true)
    @Mapping(target = "houseHistories", ignore = true)
    @Mapping(target = "owner",source = "isOwner")
    void updateModel(RequestDtoPerson requestDtoPerson, @MappingTarget Person person);
}
