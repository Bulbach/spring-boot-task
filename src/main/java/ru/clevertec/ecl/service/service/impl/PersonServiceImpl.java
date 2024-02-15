package ru.clevertec.ecl.service.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.cache.annotation.CustomCachableGet;
import ru.clevertec.ecl.cache.annotation.CustomCachebleCreate;
import ru.clevertec.ecl.cache.annotation.CustomCachebleDelete;
import ru.clevertec.ecl.cache.annotation.CustomCachebleUpdate;
import ru.clevertec.ecl.dto.requestDto.RequestDtoPerson;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Person;
import ru.clevertec.ecl.exception.HouseNotFoundException;
import ru.clevertec.ecl.exception.PersonNotFoundException;
import ru.clevertec.ecl.mapper.HouseMapper;
import ru.clevertec.ecl.mapper.PersonMapper;
import ru.clevertec.ecl.repository.jpa.HouseJpaRepository;
import ru.clevertec.ecl.repository.jpa.PersonJpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService<ResponseDtoPerson, RequestDtoPerson> {

    private final HouseJpaRepository houseJpaRepository;
    private final PersonMapper personMapper;
    private final HouseMapper houseMapper;
    private final PersonJpaRepository jpaRepository;

    public Collection<ResponseDtoPerson> getAllPersons(int size) {

        return jpaRepository.findAll().stream()
                .map(personMapper::toDto)
                .limit(size - 1)
                .collect(Collectors.toList());
    }

    @CustomCachableGet
    public ResponseDtoPerson getById(UUID uuid) {

        Person person = jpaRepository.findByUuid(uuid)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with uuid: " + uuid));
        return personMapper.toDto(person);
    }

    @Transactional
    @CustomCachebleCreate
    public ResponseDtoPerson create(RequestDtoPerson requestDtoPerson) {

        Person person = personMapper.toModel(requestDtoPerson);

        if (!isPersonUniqueByPassport(person)) {
            throw new RuntimeException("Person with the same details already exists");
        }

        person.setUuid(UUID.randomUUID());
        person.setCreateDate(LocalDateTime.now());
        person.setUpdateDate(LocalDateTime.now());
        UUID houseUuid = person.getHouse().getUuid();
        House houseById = houseJpaRepository.findByUuid(houseUuid)
                .orElseThrow(() -> new HouseNotFoundException("House not found with id: " + houseUuid));
        List<House> ownedHouses = jpaRepository.findOwnedHousesByPersonId(person.getUuid());
        if (person.isOwner() && !ownedHouses.contains(houseById)) {
            ownedHouses.add(houseById);
        } else {
            person.setHouse(houseById);
        }
        person.setHouse(houseById);

        Person savedPerson = jpaRepository.save(person);
        return personMapper.toDto(savedPerson);
    }

    @Transactional
    @CustomCachebleUpdate
    public ResponseDtoPerson update(UUID uuid, RequestDtoPerson person) {

        Person existingPerson = jpaRepository.findByUuid(uuid)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with uuid: " + uuid));

        House residenceHouse = houseJpaRepository.findByUuid(person.house().getUuid())
                .orElseThrow(() -> new HouseNotFoundException("House not found with uuid: " + person.house().getUuid()));

        List<House> ownedHouses = jpaRepository.findOwnedHousesByPersonId(uuid);

        personMapper.updateModel(person, existingPerson);

        if (isOwner(person) && !ownedHouses.contains(residenceHouse)) {
            existingPerson.addOwnedHouse(residenceHouse);
        }
        existingPerson.setHouse(residenceHouse);

        Person updatedPerson = jpaRepository.save(existingPerson);

        return personMapper.toDto(updatedPerson);
    }

    @Transactional
    @CustomCachebleDelete
    public void delete(UUID uuid) {
        Person person = jpaRepository.findByUuid(uuid)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with uuid: " + uuid));
        jpaRepository.delete(person);
    }

    public List<ResponseDtoHouse> getHousesByPersonId(UUID personId) {

        return jpaRepository.findHousesByPersonId(personId)
                .stream()
                .map(houseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ResponseDtoHouse> getOwnedHousesByPersonId(UUID personId) {

        return jpaRepository.findOwnedHousesByPersonId(personId)
                .stream()
                .map(houseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addOwnerToHouse(UUID personId, UUID houseId) {

        House existingHouse = houseJpaRepository.findByUuid(houseId)
                .orElseThrow(() -> new HouseNotFoundException("House not found with id: " + houseId));
        Person existingPerson = jpaRepository.findByUuid(personId)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + personId));

        Long houseLId = existingHouse.getId();
        Long personLId = existingPerson.getId();

        jpaRepository.addOwnerToHouse(houseLId, personLId);

    }

    private boolean isOwner(RequestDtoPerson dtoPerson) {
        return dtoPerson.isOwner();
    }

    private boolean isPersonUniqueByPassport(Person person) {
        Optional<Long> personByPassportWithin = jpaRepository.countPersonByPassport(person.getPassport());
        Long countPersonByPassport = personByPassportWithin.get();
        return countPersonByPassport == 0;
    }

}