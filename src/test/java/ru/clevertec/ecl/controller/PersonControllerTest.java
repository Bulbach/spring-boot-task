package ru.clevertec.ecl.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PathVariable;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.clevertec.ecl.PostgresSqlContainerInitialization;
import ru.clevertec.ecl.dto.requestDto.RequestDtoPerson;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Passport;
import ru.clevertec.ecl.entity.Person;
import ru.clevertec.ecl.service.service.impl.PersonServiceImpl;
import ru.clevertec.ecl.util.HouseTestBuilder;
import ru.clevertec.ecl.util.PersonTestBuilder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest()
@AutoConfigureMockMvc
//@NoArgsConstructor
@RequiredArgsConstructor
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonControllerTest extends PostgresSqlContainerInitialization {

    @LocalServerPort
    private int port;
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private PersonController personController;

    @AfterEach
    public void resetDb() {
//        repository.deleteAll();
    }

    private final PersonServiceImpl personServiceImpl;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAllPersons() {
        // when
        int size = 15;
        ResponseEntity<ResponseDtoPerson[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/persons?size=" + size, ResponseDtoPerson[].class);

        ResponseDtoPerson[] persons = response.getBody();
        int expected = persons.length;

        // given
        Collection<ResponseDtoPerson> allPersons = personServiceImpl.getAllPersons(size);
        int actual = allPersons.size();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(actual, expected);
        assertNotNull(persons);
        assertThat(expected).isLessThan(size);
    }

    @Test
    void getById() {

        // when
        Person buildPerson = PersonTestBuilder.buildPerson();
        ResponseEntity<ResponseDtoPerson> expected = restTemplate.getForEntity(
                "http://localhost:" + port + "/persons/" + buildPerson.getUuid(), ResponseDtoPerson.class);
        // given
        ResponseDtoPerson actual = personServiceImpl.getById(buildPerson.getUuid());

        // then
        assertEquals(actual.passport(), expected.getBody().passport());
        assertThat(actual)
                .hasFieldOrPropertyWithValue("name", expected.getBody().name())
                .hasFieldOrPropertyWithValue("surname", expected.getBody().surname())
                .hasFieldOrPropertyWithValue("sex", expected.getBody().sex())
                .hasFieldOrPropertyWithValue("passport", expected.getBody().passport())
        ;
        assertThat(expected.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(expected.getBody()).isNotNull();
    }

    @Test
    void createPerson() {

        // when
        House house = HouseTestBuilder.testHouse();

        RequestDtoPerson requestDtoPerson = PersonTestBuilder.builder().build().requestDtoPerson(house);

        // given
        ResponseDtoPerson actual = personServiceImpl.create(requestDtoPerson);

        // then
        assertEquals(actual.passport(), requestDtoPerson.passport());

    }

    @Test
    void createPersonByTestTemplate() {

        // when
        House house = HouseTestBuilder.testHouse();

        RequestDtoPerson requestDtoPerson = PersonTestBuilder.builder().build().requestDtoPerson(house);

        ResponseEntity<ResponseDtoPerson> expected = restTemplate.postForEntity(
                "http://localhost:" + port + "/persons", requestDtoPerson, ResponseDtoPerson.class);

        // given

        // then
        assertThat(expected.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(expected.getBody()).isNotNull();
        assertEquals(requestDtoPerson.passport(), expected.getBody().passport());

    }

    @Test
    void updatePerson() {

        // when
        Person person = PersonTestBuilder.buildPerson();
        person.setName("testName");
        person.setSurname("testSurname");
        RequestDtoPerson requestDtoPerson = new RequestDtoPerson(person.getUuid().toString(),
                person.getName(),
                person.getSurname(),
                person.getSex(),
                person.getCreateDate(),
                LocalDateTime.now(),
                new Passport(person.getUuid().toString().substring(0, 5), person.getUuid().toString().substring(5, 10)),
                person.getHouse(),
                false);

        // given
        ResponseDtoPerson update = personServiceImpl.update(UUID.fromString(requestDtoPerson.uuid()), requestDtoPerson);

        // then
        assertNotEquals(PersonTestBuilder.buildPerson().getName(), update.name());
        assertNotEquals(PersonTestBuilder.buildPerson().getSurname(), update.surname());
    }

    @Test
    void addOwnerToHouse() {

        // when
        UUID houseId = UUID.fromString("b7d69c82-9833-4364-9c77-a0ecfc467c63");
        UUID personId = UUID.fromString("fd668dac-ad7f-4a24-87a7-c49b435f74bb");

        // given
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/persons/" + personId + "/houses/" + houseId, HttpMethod.PUT, null, Void.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deletePerson() {

        // when
        UUID personId = UUID.fromString("fd668dac-ad7f-4a24-87a7-c49b435f74bb");
        // given
        restTemplate.delete("http://localhost:" + port + "/persons/" + personId);
        // then
        ResponseEntity<ResponseDtoPerson> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/persons/" + personId, ResponseDtoPerson.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getHousesByPersonId() {

        // when
        UUID personId = UUID.fromString("fd668dac-ad7f-4a24-87a7-c49b435f74bb");
        // given
        ResponseEntity<ResponseDtoHouse[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/persons/" + personId + "/houses", ResponseDtoHouse[].class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }


    @Test
    void getOwnedHousesByPersonId() {

        // when
        UUID personId = UUID.fromString("fd668dac-ad7f-4a24-87a7-c49b435f74bb");
        // given
        ResponseEntity<ResponseDtoHouse[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/persons/" + personId + "/owned-houses", ResponseDtoHouse[].class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

}