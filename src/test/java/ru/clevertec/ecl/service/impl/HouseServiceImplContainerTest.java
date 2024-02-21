package ru.clevertec.ecl.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.PostgresSqlContainerInitialization;
import ru.clevertec.ecl.dto.requestDto.RequestDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Person;
import by.bulbach.exceptionspringbootstarter.exception.HouseNotFoundException;
import ru.clevertec.ecl.mapper.HouseMapper;
import ru.clevertec.ecl.mapper.PersonMapper;
import ru.clevertec.ecl.repository.jpa.HouseJpaRepository;
import ru.clevertec.ecl.repository.jpa.PersonJpaRepository;
import ru.clevertec.ecl.service.HouseService;
import ru.clevertec.ecl.util.HouseTestBuilder;
import ru.clevertec.ecl.util.PersonTestBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class HouseServiceImplContainerTest extends PostgresSqlContainerInitialization {

    @SpyBean
    private HouseJpaRepository houseRepository;
    @SpyBean
    private PersonJpaRepository personJpaRepository;

    private final HouseMapper houseMapper;

    private final PersonMapper personMapper;


    private final HouseService<ResponseDtoHouse, RequestDtoHouse> houseServiceImpl;
    ;

    @Test
    void getAllHouses_Success() {

        // when
        List<House> houses = HouseTestBuilder.builder().build().houses();
        houseRepository.saveAll(houses);
        List<ResponseDtoHouse> dtoHouses = houses.stream().map(houseMapper::toDto).toList();

        // given
        Collection<ResponseDtoHouse> actual = houseServiceImpl.getAll(3);

        // then
        assertThat(actual).hasSize(3);
        assertThat(actual).isNotEmpty();
    }

    @Test
    void getById_success() {

        // when
        House house = HouseTestBuilder.testHouse();
        house.setUuid(UUID.fromString("62672de0-6061-4950-ac08-fea4fefdb7de"));
        houseRepository.save(house);
        ResponseDtoHouse dtoHouse = houseMapper.toDto(house);

        // given
        ResponseDtoHouse actual = houseServiceImpl.getById(house.getUuid());

        // then
        assertThat(actual.uuid()).isEqualTo(dtoHouse.uuid());
        assertThat(actual).isNotNull();
    }

    @Test
    void getResidents_success() {

        // when
        House house = HouseTestBuilder.testHouse();
        houseRepository.save(house);

        Person onePerson = PersonTestBuilder.builder().build().persons().get(1);
        onePerson.setHouse(house);
        personJpaRepository.save(onePerson);
        ResponseDtoPerson oneDtoPerson = personMapper.toDto(onePerson);

        Person twoPerson = PersonTestBuilder.builder().build().persons().get(2);
        twoPerson.setHouse(house);
        personJpaRepository.save(twoPerson);
        ResponseDtoPerson twoDtoPerson = personMapper.toDto(twoPerson);

        List<ResponseDtoPerson> exitedResidents = Arrays.asList(oneDtoPerson, twoDtoPerson);

        // given
        Collection<ResponseDtoPerson> actualResidents = houseServiceImpl.getResidents(house.getUuid());

        // then
        boolean allMatch = actualResidents.stream()
                .allMatch(actual -> exitedResidents.stream()
                        .anyMatch(expected ->
                                expected.uuid().equals(actual.uuid())
                                        && expected.name().equals(actual.name())
                                        && expected.surname().equals(actual.surname())
                                        && expected.passport().equals(actual.passport())
                        ));

        assertTrue(allMatch);
        assertThat(actualResidents).isNotEmpty();
        assertThat(actualResidents).hasSize(2);
    }

    @Test
    void create_success() {

        // when
        RequestDtoHouse requestDtoHouse = HouseTestBuilder.builder().build().buildRequestDtoHouse();
        ResponseDtoHouse responseDtoHouseExited = HouseTestBuilder.builder().build().buildResponseDtoHouse();

        // given
        ResponseDtoHouse responseDtoHouseActual = houseServiceImpl.create(requestDtoHouse);

        // then
        assertThat(responseDtoHouseActual).isEqualTo(responseDtoHouseExited);
    }

    @Test
    void updateHouse_success() {

        // when
        House house = HouseTestBuilder.testHouse();
        houseRepository.save(house);
        house.setStreet("updateStreet");
        house.setCity("updateCity");
        house.setCountry("updateCountry");
        RequestDtoHouse requestDtoHouse =
                new RequestDtoHouse(house.getUuid().toString()
                        , house.getArea()
                        , house.getCountry()
                        , house.getCity()
                        , house.getStreet()
                        , house.getHouseNumber()
                        , house.getCreateDate()

                );
        ResponseDtoHouse responseDtoHouseUpdate = houseMapper.toDto(house);

        // given
        ResponseDtoHouse actual = houseServiceImpl.update(house.getUuid(), requestDtoHouse);

        // then
        assertThat(actual).isEqualTo(responseDtoHouseUpdate);

    }

    @Test
    void delete() {
        // when
        House house = HouseTestBuilder.testHouse();
        houseRepository.save(house);
        House existingHouse = houseRepository.findByUuid(house.getUuid())
                .orElseThrow(() -> new HouseNotFoundException("House not found with id: " + house.getUuid()));


        // then
        assertThatCode(() -> houseServiceImpl.delete(existingHouse.getUuid())).doesNotThrowAnyException();
    }

    @Test
    void getTenantsByHouseId_success() {

        // when
        House house = HouseTestBuilder.testHouse();
        houseRepository.save(house);

        Person onePerson = PersonTestBuilder.builder().build().persons().get(1);
        onePerson.setHouse(house);
        personJpaRepository.save(onePerson);
        ResponseDtoPerson oneDtoPerson = personMapper.toDto(onePerson);

        Person twoPerson = PersonTestBuilder.builder().build().persons().get(2);
        twoPerson.setHouse(house);
        personJpaRepository.save(twoPerson);
        ResponseDtoPerson twoDtoPerson = personMapper.toDto(twoPerson);

        List<ResponseDtoPerson> exitedTenants = Arrays.asList(oneDtoPerson, twoDtoPerson);

        // given
        List<ResponseDtoPerson> tenantsByHouseId = houseServiceImpl.getTenantsByHouseId(house.getUuid());

        // then
        boolean allMatch = tenantsByHouseId.stream()
                .allMatch(actual -> exitedTenants.stream()
                        .anyMatch(expected ->
                                expected.uuid().equals(actual.uuid())
                                        && expected.name().equals(actual.name())
                                        && expected.surname().equals(actual.surname())
                                        && expected.passport().equals(actual.passport())
                        ));

        assertTrue(allMatch);
        assertThat(tenantsByHouseId).isNotEmpty();
        assertThat(tenantsByHouseId).hasSize(2);

    }

    @Test
    void getOwnersByHouseId_success() {

        // when
        House house = HouseTestBuilder.testHouse();
        houseRepository.save(house);
        Long houseId = house.getId();

        List<Person> owners = HouseTestBuilder.buildOwners();
        owners.stream().peek(person -> person.setHouse(house)).collect(Collectors.toList());
        personJpaRepository.saveAll(owners);

        List<ResponseDtoPerson> dtoPersonListOwnersExited = owners.stream()
                .peek(person -> personJpaRepository.addOwnerToHouse(houseId, person.getId()))
                .map(personMapper::toDto)
                .collect(Collectors.toList());

        // given
        List<ResponseDtoPerson> ownersByHouseId = houseServiceImpl.getOwnersByHouseId(house.getUuid());

        // then
        boolean allMatch = ownersByHouseId.stream()
                .allMatch(actual -> dtoPersonListOwnersExited.stream()
                        .anyMatch(expected ->
                                expected.uuid().equals(actual.uuid())
                                        && expected.name().equals(actual.name())
                                        && expected.surname().equals(actual.surname())
                                        && expected.passport().equals(actual.passport())
                        ));

        assertTrue(allMatch);
        assertThat(ownersByHouseId).isNotEmpty();
        assertThat(ownersByHouseId).hasSize(2);

    }
}
