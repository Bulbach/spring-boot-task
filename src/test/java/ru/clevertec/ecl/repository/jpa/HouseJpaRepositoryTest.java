package ru.clevertec.ecl.repository.jpa;

import lombok.RequiredArgsConstructor;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HouseJpaRepositoryTest extends PostgresSqlContainerInitialization {

    private final HouseJpaRepository houseJpaRepository;

    private final PersonJpaRepository personJpaRepository;

    @Test
    void findByUuid() {
        // when
        UUID houseId = UUID.fromString("7d0fb9b2-2e52-4137-b325-770bd9660282");
        House expected = HouseTestBuilder.testHouse();
        expected.setUuid(houseId);
        houseJpaRepository.save(expected);

        // when
        House actual = houseJpaRepository.findByUuid(houseId)
                .orElseThrow(() -> new HouseNotFoundException("House not found with id: " + houseId));

        // then
        assertThat(actual).isEqualTo(expected);

    }

    @Test
    void findTenantsByHouseId_shouldReturnListOfTenants() {

        // when
        House house = HouseTestBuilder.testHouse();
        houseJpaRepository.save(house);
        Person personOne = PersonTestBuilder.builder().build().persons().get(1);
        personOne.setHouse(house);
        personJpaRepository.save(personOne);

        Person personTwo = PersonTestBuilder.builder().build().persons().get(0);
        personTwo.setHouse(house);
        personJpaRepository.save(personTwo);

        // given
        List<Person> result = houseJpaRepository.findTenantsByHouseId(house.getUuid());

        // then
        assertEquals(2, result.size());
    }


    @Test
    void findOwnersByHouseId_shouldReturnListOfOwners() {

        // when
        House tenantHouse = HouseTestBuilder.builder().build().houses().get(1);
        houseJpaRepository.save(tenantHouse);

        House save = houseJpaRepository.save(HouseTestBuilder.testHouse());

        UUID one = UUID.fromString("81a1a01f-7ceb-4535-879f-5f3cff68479f");
        UUID two = UUID.fromString("5ff2c821-a086-46bf-a4b1-55e43c91a885");

        Person personOne = PersonTestBuilder.builder().build().persons().get(1);
        personOne.setUuid(one);
        personOne.setHouse(tenantHouse);
        personJpaRepository.save(personOne);

        Person personTwo = PersonTestBuilder.builder().build().persons().get(0);
        personTwo.setUuid(two);
        personTwo.setHouse(tenantHouse);
        personJpaRepository.save(personTwo);

        Long homeId = save.getId();
        Long personIdOne = personOne.getId();
        Long personIdTwo = personTwo.getId();

        personJpaRepository.addOwnerToHouse(homeId, personIdOne);
        personJpaRepository.addOwnerToHouse(homeId, personIdTwo);

        // given
        List<Person> result = houseJpaRepository.findOwnersByHouseId(save.getUuid());

        // then
        assertEquals(2, result.size());
    }
}