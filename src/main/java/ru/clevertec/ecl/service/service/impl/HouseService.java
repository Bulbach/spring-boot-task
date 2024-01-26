package ru.clevertec.ecl.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.cache.annotation.CustomCachableHouseGet;
import ru.clevertec.ecl.cache.annotation.CustomCachebleHouseCreate;
import ru.clevertec.ecl.cache.annotation.CustomCachebleHouseDelete;
import ru.clevertec.ecl.cache.annotation.CustomCachebleHouseUpdate;
import ru.clevertec.ecl.dto.requestDto.RequestDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.exception.HouseNotFoundException;
import ru.clevertec.ecl.mapper.HouseMapper;
import ru.clevertec.ecl.mapper.PersonMapper;
import ru.clevertec.ecl.repository.impl.HouseRepositoryImpl;
import ru.clevertec.ecl.repository.jpa.HouseJpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HouseService {

    private final HouseRepositoryImpl houseRepository;
    private final HouseMapper houseMapper;
    private final PersonMapper personMapper;
    private final HouseJpaRepository jpaRepository;


    public Collection<ResponseDtoHouse> getAll(int size) {

        return jpaRepository.findAll().stream()
                .map(houseMapper::toDto)
                .limit(size)
                .collect(Collectors.toList());
    }

    @CustomCachableHouseGet
    public ResponseDtoHouse getById(UUID id) {

        House house = jpaRepository.findById(id)
                .orElseThrow(() -> new HouseNotFoundException("House not found with id: " + id));

        return houseMapper.toDto(house);
    }

    public Collection<ResponseDtoPerson> getResidents(UUID houseId) {

        return jpaRepository.findTenantsByHouseId(houseId)
                .stream()
                .map(personMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @CustomCachebleHouseCreate
    public ResponseDtoHouse create(RequestDtoHouse requestDtoHouse) {

        House house = houseMapper.toModel(requestDtoHouse);
        House savedHouse = jpaRepository.save(house);

        return houseMapper.toDto(savedHouse);
    }

    @Transactional
    @CustomCachebleHouseUpdate
    public ResponseDtoHouse update(UUID id, RequestDtoHouse requestDtoHouse) {

        House existingHouse = jpaRepository.findById(id)
                .orElseThrow(() -> new HouseNotFoundException("House not found with id: " + id));

        houseMapper.updateModel(requestDtoHouse, existingHouse);
        House updatedHouse = jpaRepository.save(existingHouse);

        return houseMapper.toDto(updatedHouse);
    }

    @Transactional
    @CustomCachebleHouseDelete
    public void delete(UUID id) {
        houseRepository.delete(id);
    }


    public List<ResponseDtoPerson> getTenantsByHouseId(UUID houseId) {

        return jpaRepository.findTenantsByHouseId(houseId)
                .stream()
                .map(personMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ResponseDtoPerson> getOwnersByHouseId(UUID houseId) {

        return jpaRepository.findOwnersByHouseId(houseId)
                .stream()
                .map(personMapper::toDto)
                .collect(Collectors.toList());
    }
}
