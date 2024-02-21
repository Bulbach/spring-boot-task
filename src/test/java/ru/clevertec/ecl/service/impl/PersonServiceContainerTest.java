package ru.clevertec.ecl.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.PostgresSqlContainerInitialization;
import ru.clevertec.ecl.dto.requestDto.RequestDtoPerson;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Passport;
import ru.clevertec.ecl.entity.Person;
import by.bulbach.exceptionspringbootstarter.exception.PersonNotFoundException;
import ru.clevertec.ecl.mapper.HouseMapper;
import ru.clevertec.ecl.mapper.PersonMapper;
import ru.clevertec.ecl.repository.jpa.HouseJpaRepository;
import ru.clevertec.ecl.repository.jpa.PersonJpaRepository;
import ru.clevertec.ecl.service.PersonService;
import ru.clevertec.ecl.util.HouseTestBuilder;
import ru.clevertec.ecl.util.PersonTestBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class PersonServiceContainerTest extends PostgresSqlContainerInitialization {
    @Autowired
    private HouseJpaRepository houseJpaRepository;
    @Autowired
    private PersonJpaRepository personJpaRepository;
    private final HouseMapper houseMapper;

    private final PersonMapper personMapper;

    private final PersonService<ResponseDtoPerson, RequestDtoPerson> personService;

    @Test
    void getAllPersons_Success() {
        // when
        int size = 5;
        House house = HouseTestBuilder.testHouse();
        houseJpaRepository.save(house);

        List<Person> persons = PersonTestBuilder.builder().build().persons();
        persons.stream().peek(person -> person.setHouse(house)).collect(Collectors.toList());
        personJpaRepository.saveAll(persons);

        // given
        Collection<ResponseDtoPerson> actualPersons = personService.getAllPersons(size);

        // then
        assertThat(actualPersons).hasSizeLessThanOrEqualTo(size);
    }

    @Test
    void getById_Success() {
        // when
        House house = HouseTestBuilder.testHouse();
        houseJpaRepository.save(house);

        Person person = PersonTestBuilder.builder().build().persons().get(0);
        person.setHouse(house);
        person.setUuid(UUID.randomUUID());
        personJpaRepository.save(person);
        ResponseDtoPerson responseDtoPersonExpected = personMapper.toDto(person);

        // given
        ResponseDtoPerson actualDtoPerson = personService.getById(person.getUuid());

        // then
        assertThat(actualDtoPerson).isNotNull();
        assertThat(actualDtoPerson.uuid()).isEqualTo(responseDtoPersonExpected.uuid());
    }

    @Test
    void createPerson_success() {

        // when
        House house = HouseTestBuilder.builder().build().houses().get(0);
        houseJpaRepository.save(house);
        Person person = PersonTestBuilder.buildPerson();
        RequestDtoPerson requestDtoPerson = PersonTestBuilder.builder().build().buildRequestDtoPerson(house, person);

        // given
        ResponseDtoPerson actual = personService.create(requestDtoPerson);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.passport()).isEqualTo(requestDtoPerson.passport());
    }

    @Test
    void updatePerson_success() {

        // when
        House house = HouseTestBuilder.builder().build().houses().get(0);
        houseJpaRepository.save(house);

        UUID personId = UUID.fromString("feda712b-54b8-4e9e-ba67-fbc5665c3cab");
        Person personExited = PersonTestBuilder.builder().build().persons().get(0);
        personExited.setHouse(house);
        personExited.setUuid(personId);
        personJpaRepository.save(personExited);

        RequestDtoPerson requestDtoPerson = new RequestDtoPerson(
                "feda712b-54b8-4e9e-ba67-fbc5665c3cab",
                "John",
                "Doe",
                Person.Sex.Male,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Passport("54b8", "fbc56"),
                house,
                false
        );

        // given
        ResponseDtoPerson updateDtoPerson = personService.update(personId, requestDtoPerson);

        // then
        assertThat(updateDtoPerson).isNotNull();
        assertThat(updateDtoPerson.name()).isEqualTo(requestDtoPerson.name());
        assertThat(updateDtoPerson.surname()).isEqualTo(requestDtoPerson.surname());
        assertThat(updateDtoPerson.passport()).isEqualTo(requestDtoPerson.passport());
    }

    //    @Test
    void deletePerson_success() {

        // when
        House house = HouseTestBuilder.testHouse();
        houseJpaRepository.save(house);

        Person person = PersonTestBuilder.builder().build().persons().get(0);
        person.setHouse(house);
        person.setUuid(UUID.randomUUID());
        personJpaRepository.saveAndFlush(person);
        // given

        personService.delete(person.getUuid());

        // then
        assertThrows(PersonNotFoundException.class, () -> personService.getById(person.getUuid()));
    }

    @Test
    void getHousesByPersonId_success() {

        // when
        House house = HouseTestBuilder.testHouse();
        houseJpaRepository.save(house);
        Person person = PersonTestBuilder.buildPerson();
        person.setHouse(house);
        personJpaRepository.saveAndFlush(person);

        House houseOne = HouseTestBuilder.builder().build().houses().get(0);
        houseJpaRepository.save(houseOne);
        person.setHouse(houseOne);
        personJpaRepository.saveAndFlush(person);

        House houseTwo = HouseTestBuilder.builder().build().houses().get(1);
        houseJpaRepository.save(houseTwo);
        person.setHouse(houseTwo);
        personJpaRepository.saveAndFlush(person);

        List<House> houses = Arrays.asList(house, houseOne, houseTwo);
        List<ResponseDtoHouse> responseDtoHouses = houses.stream().map(houseMapper::toDto).toList();

        // given
        List<ResponseDtoHouse> housesByPersonId = personService.getHousesByPersonId(person.getUuid());

        // then
        boolean allMatch = housesByPersonId.stream()
                .allMatch(actual -> responseDtoHouses.stream()
                        .anyMatch(expected ->
                                expected.uuid().equals(actual.uuid())
                                        && expected.area().equals(actual.area())
                                        && expected.country().equals(actual.country())
                                        && expected.city().equals(actual.city())
                        ));

        assertTrue(allMatch);
    }

    @Test
    void getOwnedHousesByPersonId_success() {

        // when
        House house = HouseTestBuilder.testHouse();
        houseJpaRepository.save(house);
        Person person = PersonTestBuilder.buildPerson();
        person.setHouse(house);
        personJpaRepository.saveAndFlush(person);

        House houseOne = HouseTestBuilder.builder().build().houses().get(0);
        houseJpaRepository.save(houseOne);
        House houseTwo = HouseTestBuilder.builder().build().houses().get(1);
        houseJpaRepository.save(houseTwo);

        personJpaRepository.addOwnerToHouse(houseOne.getId(), person.getId());
        personJpaRepository.addOwnerToHouse(houseTwo.getId(), person.getId());

        List<House> houses = Arrays.asList(houseOne, houseTwo);
        List<ResponseDtoHouse> expectedOwnedHouse = houses.stream().map(houseMapper::toDto).toList();

        // given
        List<ResponseDtoHouse> ownedHousesByPersonId = personService.getOwnedHousesByPersonId(person.getUuid());

        // then
        boolean allMatch = ownedHousesByPersonId.stream()
                .allMatch(actual -> expectedOwnedHouse.stream()
                        .anyMatch(expected ->
                                expected.uuid().equals(actual.uuid())
                                        && expected.area().equals(actual.area())
                                        && expected.country().equals(actual.country())
                                        && expected.city().equals(actual.city())
                        ));

        assertTrue(allMatch);
    }

}
