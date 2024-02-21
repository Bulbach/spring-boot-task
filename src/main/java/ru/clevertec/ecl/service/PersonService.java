package ru.clevertec.ecl.service;

import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PersonService <D,R>{
    Collection<D> getAllPersons(int size);

    D getById(UUID uuid);

    D create(R requestDtoPerson);

    D update(UUID uuid, R person);

    void delete(UUID uuid);

    List<ResponseDtoHouse> getHousesByPersonId(UUID personId);

    List<ResponseDtoHouse> getOwnedHousesByPersonId(UUID personId);

    void addOwnerToHouse(UUID personId, UUID houseId);
}
