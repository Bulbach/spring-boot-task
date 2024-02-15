package ru.clevertec.ecl.repository.jpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestConstructor;
import ru.clevertec.ecl.PostgresSqlContainerInitialization;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Person;
import ru.clevertec.ecl.exception.HouseNotFoundException;
import ru.clevertec.ecl.util.HouseTestBuilder;
import ru.clevertec.ecl.util.PersonTestBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PersonJpaRepositoryTest extends PostgresSqlContainerInitialization {

    private final HouseJpaRepository houseJpaRepository;

    private final PersonJpaRepository personJpaRepository;

    @Test
    public void findByUuid() {

        // when
        UUID personUuid = UUID.fromString("62672de0-6061-4950-ac08-fea4fefdb7de");
        House house = HouseTestBuilder.builder().build().houses().get(2);
        houseJpaRepository.save(house);
        Person expected = PersonTestBuilder.buildPerson();
        expected.setUuid(personUuid);
        expected.setHouse(house);
        personJpaRepository.save(expected);

        // given
        Person actual = personJpaRepository.findByUuid(personUuid)
                .orElseThrow(() -> new HouseNotFoundException("Person not found with id: " + personUuid));

        // then
        assertThat(actual).isEqualTo(expected);

    }
    @Test
    void findHousesByPersonId_shouldReturnListOfTenants(){

        // when
        UUID personUuid = UUID.fromString("62672de0-6061-4950-ac08-fea4fefdb7de");
        House houseOne = HouseTestBuilder.builder().build().houses().get(2);
        houseJpaRepository.save(houseOne);
        House houseTwo = HouseTestBuilder.builder().build().houses().get(0);
        houseJpaRepository.save(houseTwo);

        Person expected = PersonTestBuilder.buildPerson();
        expected.setUuid(personUuid);
        expected.setHouse(houseOne);
        personJpaRepository.save(expected);
        expected.setHouse(houseTwo);
        personJpaRepository.save(expected);

        // given
        List<House> result = personJpaRepository.findHousesByPersonId(personUuid);

        // then
        Assertions.assertEquals(2, result.size());
    }

    @Test
    void findOwnedHousesByPersonId_shouldReturnListOfOwnedHouses(){

        // when
        UUID personUuid = UUID.fromString("62672de0-6061-4950-ac08-fea4fefdb7de");

        House houseOne = HouseTestBuilder.builder().build().houses().get(1);
        houseJpaRepository.save(houseOne);

        House houseTwo = HouseTestBuilder.builder().build().houses().get(0);
        houseJpaRepository.save(houseTwo);

        Person expected = PersonTestBuilder.buildPerson();
        expected.setUuid(personUuid);
        expected.setHouse(houseOne);
        personJpaRepository.save(expected);

        Long oneHouseId = houseOne.getId();
        Long twoHouseId = houseTwo.getId();
        Long personId = expected.getId();

        personJpaRepository.addOwnerToHouse(oneHouseId, personId);
        personJpaRepository.addOwnerToHouse(twoHouseId, personId);

        // given
        List<House> result = personJpaRepository.findOwnedHousesByPersonId(personUuid);

        // then
        Assertions.assertEquals(2, result.size());
    }
@Test
    void addOwnerToHouse_success(){

        UUID personUuid = UUID.fromString("62672de0-6061-4950-ac08-fea4fefdb7de");
        House house = HouseTestBuilder.builder().build().houses().get(1);
        houseJpaRepository.save(house);

        Person expected = PersonTestBuilder.buildPerson();
        expected.setUuid(personUuid);
        expected.setHouse(house);
        personJpaRepository.save(expected);

        Long houseId = house.getId();
        Long personId = expected.getId();

        // then
        personJpaRepository.addOwnerToHouse(houseId, personId);

        Optional<House> updatedHouse = houseJpaRepository.findByUuid(house.getUuid());
        assertThat(updatedHouse).isPresent();
        assertThat(houseJpaRepository.findOwnersByHouseId(updatedHouse.get().getUuid())).hasSize(1);
    }

}
