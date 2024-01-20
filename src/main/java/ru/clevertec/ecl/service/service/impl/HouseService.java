package ru.clevertec.ecl.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.requestDto.RequestDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.exception.HouseNotFoundException;
import ru.clevertec.ecl.mapper.HouseMapper;
import ru.clevertec.ecl.mapper.PersonMapper;
import ru.clevertec.ecl.repository.impl.HouseRepositoryImpl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseService {

    private final HouseRepositoryImpl houseRepository;
    private final HouseMapper houseMapper;
    private final PersonMapper personMapper;


    public Collection<ResponseDtoHouse> getAll(int size) {
        return houseRepository.findAll().stream()
                .map(house -> houseMapper.toDto(house))
                .limit(size)
                .collect(Collectors.toList());
    }

    public ResponseDtoHouse getById(UUID id) {
        House house = houseRepository.findById(id)
                .orElseThrow(() -> new HouseNotFoundException("House not found with id: " + id));
        return houseMapper.toDto(house);
    }

    public Collection<ResponseDtoPerson> getResidents(UUID houseId) {
        return houseRepository.getResidents(houseId)
                .stream()
                .map(person -> personMapper.toDto(person))
                .collect(Collectors.toList());
    }

    public ResponseDtoHouse create(RequestDtoHouse requestDtoHouse) {
        House house = houseMapper.toModel(requestDtoHouse);
        House savedHouse = houseRepository.create(house);
        return houseMapper.toDto(savedHouse);
    }

    public ResponseDtoHouse update(UUID id, RequestDtoHouse requestDtoHouse) {
        House existingHouse = houseRepository.findById(id)
                .orElseThrow(() -> new HouseNotFoundException("House not found with id: " + id));

        houseMapper.updateModel(requestDtoHouse, existingHouse);
        House updatedHouse = houseRepository.update(existingHouse);
        return houseMapper.toDto(updatedHouse);
    }

    public void delete(UUID id) {
        houseRepository.delete(id);
    }
}
