package ru.clevertec.ecl.service.service.impl;

import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface HouseService<T, R> {

    Collection<T> getAll(int size);

    T getById(UUID uuid);

    Collection<ResponseDtoPerson> getResidents(UUID houseUuid);

    T create(R requestDtoHouse);

    T update(UUID uuid, R requestDtoHouse);

    void delete(UUID uuid);

    List<ResponseDtoPerson> getTenantsByHouseId(UUID houseId);

    List<ResponseDtoPerson> getOwnersByHouseId(UUID houseId);
}
