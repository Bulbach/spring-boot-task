package ru.clevertec.ecl.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.requestDto.RequestDtoPerson;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Person;
import ru.clevertec.ecl.exception.PersonNotFoundException;
import ru.clevertec.ecl.mapper.HouseMapper;
import ru.clevertec.ecl.mapper.PersonMapper;
import ru.clevertec.ecl.repository.impl.PersonRepositoryImpl;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepositoryImpl personRepository;
    private final PersonMapper personMapper;
    private final HouseMapper houseMapper;

    public Collection<ResponseDtoPerson> getAllPersons(int size) {
        return personRepository.findAll().stream()
                .map(person -> personMapper.toDto(person))
                .limit(size)
                .collect(Collectors.toList());
    }

    public ResponseDtoPerson getById(UUID id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
        return personMapper.toDto(person);
    }

    public Collection<ResponseDtoHouse> getOwnedHouses(UUID personId) {
        return personRepository.getOwnedHouses(personId)
                .stream()
                .map(house -> houseMapper.toDto(house))
                .collect(Collectors.toList());
    }

    public ResponseDtoPerson create(RequestDtoPerson requestDtoPerson) {
        Person person = personMapper.toModel(requestDtoPerson);
        Person savedPerson = personRepository.create(person);
        return personMapper.toDto(savedPerson);
    }

    public ResponseDtoPerson update(UUID id, RequestDtoPerson requestDtoPerson) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));

        personMapper.updateModel(requestDtoPerson, existingPerson);
        Person updatedPerson = personRepository.update(existingPerson);
        return personMapper.toDto(updatedPerson);
    }

    public void delete(UUID id) {
        personRepository.delete(id);
    }
}