package ru.clevertec.ecl.service.service.impl;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.ecl.dto.requestDto.RequestDtoPerson;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Passport;
import ru.clevertec.ecl.entity.Person;
import ru.clevertec.ecl.exception.PersonNotFoundException;
import ru.clevertec.ecl.mapper.HouseMapper;
import ru.clevertec.ecl.mapper.PersonMapper;
import ru.clevertec.ecl.repository.jpa.HouseJpaRepository;
import ru.clevertec.ecl.repository.jpa.PersonJpaRepository;
import ru.clevertec.ecl.util.HouseTestBuilder;
import ru.clevertec.ecl.util.PersonTestBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceImplTest {

    private PersonJpaRepository personRepository = mock(PersonJpaRepository.class);

    private HouseJpaRepository houseRepository = mock(HouseJpaRepository.class);
    @Mock
    private PersonMapper personMapper;
    @Mock
    private HouseMapper houseMapper;
    @Mock
    private SessionFactory sessionFactory;
    @InjectMocks
    private PersonServiceImpl personServiceImpl;


    @Test
    void getAllPersons() {
        // given
        List<Person> mockPersons =
                Arrays.asList(
                        PersonTestBuilder.builder().build().persons().get(0),
                        PersonTestBuilder.builder().build().persons().get(1));
        when(personRepository.findAll()).thenReturn(mockPersons);
        when(personMapper.toDto(any())).thenReturn(new ResponseDtoPerson(
                "feda712b-54b8-4e9e-ba67-fbc5665c3cab",
                "John",
                "Doe",
                "Male",
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Passport("AB123", "456789")
        ));

        // when
        Collection<ResponseDtoPerson> result = personServiceImpl.getAllPersons(10);

        // then
        assertEquals(2, result.size());
        verify(personRepository, times(1)).findAll();
        verify(personMapper, times(2)).toDto(any());
    }

    @Test
    void getById() {
        // given
        UUID personId = UUID.fromString("c8522e3e-d05f-4720-8cfc-fa3afebf9349");
        Person mockPerson = PersonTestBuilder.builder().build().persons().get(0);
        when(personRepository.findById(personId)).thenReturn(Optional.of(mockPerson));
        when(personMapper.toDto(any())).thenReturn(new ResponseDtoPerson(
                "c8522e3e-d05f-4720-8cfc-fa3afebf9349",
                "John",
                "Doe",
                "Male",
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Passport("AB123", "456789")
        ));

        // when
        ResponseDtoPerson result = personServiceImpl.getById(personId);

        // then
        assertNotNull(result);
        verify(personRepository, times(1)).findById(personId);
        verify(personMapper, times(1)).toDto(any());
    }

    @Test
    void getOwnedHouses() {
        // given
        UUID personId = UUID.fromString("feda712b-54b8-4e9e-ba67-fbc5665c3cab");
        List<House> mockOwnedHouses =
                Arrays.asList(
                        HouseTestBuilder.builder().build().houses().get(0),
                        HouseTestBuilder.builder().build().houses().get(1));

        when(personRepository.findOwnedHousesByPersonId(personId)).thenReturn(mockOwnedHouses);
        when(houseMapper.toDto(any())).thenReturn(new ResponseDtoHouse(
                "4c78be6d-1a6d-47bb-ae4a-0b63f2beccd0",
                150.3,
                "Country",
                "City",
                "Street",
                "13",
                LocalDateTime.now()
        ));

        // when
        Collection<ResponseDtoHouse> result = personServiceImpl.getOwnedHousesByPersonId(personId);

        // then
        assertEquals(2, result.size());
        verify(personRepository, times(1)).findOwnedHousesByPersonId(personId);
        verify(houseMapper, times(2)).toDto(any());
    }

    //        @Test
    void create() {
        // when
        RequestDtoPerson requestDtoPerson = new RequestDtoPerson(
                "feda712b-54b8-4e9e-ba67-fbc5665c3cab",
                "John",
                "Doe",
                Person.Sex.Male,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Passport("dfrt", "4uu789"),
                HouseTestBuilder.testHouse(),
                false
        );
        Person mockPerson = PersonTestBuilder.builder().build().buildPerson();
        when(personMapper.toModel(requestDtoPerson)).thenReturn(mockPerson);
        when(personRepository.save(mockPerson)).thenReturn(mockPerson);
        when(personMapper.toDto(mockPerson)).thenReturn(new ResponseDtoPerson(
                "feda712b-54b8-4e9e-ba67-fbc5665c3cab",
                "John",
                "Doe",
                "Male",
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Passport("AB123", "456789")
        ));

        // given
        ResponseDtoPerson result = personServiceImpl.create(requestDtoPerson);

        // then
        assertNotNull(result);
        verify(personMapper, times(1)).toModel(requestDtoPerson);
        verify(personRepository, times(1)).save(mockPerson);
        verify(personMapper, times(1)).toDto(mockPerson);
    }

    @Test
    void update() {
        // when
        UUID personId = UUID.fromString("feda712b-54b8-4e9e-ba67-fbc5665c3cab");
        House house = HouseTestBuilder.testHouse();

        RequestDtoPerson requestDtoPerson = new RequestDtoPerson(
                "feda712b-54b8-4e9e-ba67-fbc5665c3cab",
                "John",
                "Doe",
                Person.Sex.Male,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Passport("AB123", "456789"),
                house,
                true
        );
        Person existingPerson = PersonTestBuilder.buildPerson();
        existingPerson.setUuid(personId);
        existingPerson.setName("Alice");
        existingPerson.setSurname("Wonderland");
        existingPerson.setSex(Person.Sex.Female);
        existingPerson.setCreateDate(LocalDateTime.now().minusDays(1));
        existingPerson.setUpdateDate(LocalDateTime.now().minusDays(1));

        when(personRepository.findById(personId)).thenReturn(Optional.of(existingPerson));
        doNothing().when(personMapper).updateModel(requestDtoPerson, existingPerson);
        when(personRepository.save(existingPerson)).thenReturn(existingPerson);
        when(personMapper.toDto(existingPerson)).thenReturn(new ResponseDtoPerson(
                personId.toString(),
                "John",
                "Doe",
                "Male",
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Passport("AB123", "456789")
        ));

        // given
        ResponseDtoPerson result = personServiceImpl.update(personId, requestDtoPerson);

        // then
        assertNotNull(result);
        verify(personRepository, times(1)).findById(personId);
        verify(personMapper, times(1)).updateModel(requestDtoPerson, existingPerson);
        verify(personRepository, times(1)).save(existingPerson);
        verify(personMapper, times(1)).toDto(existingPerson);
    }

    @Test
    void delete() {
        // when
        UUID personId = UUID.fromString("feda712b-54b8-4e9e-ba67-fbc5665c3cab");
        Person byUuid = personRepository.findByUuid(personId)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with uuid: " + personId));
        // given
        personServiceImpl.delete(personId);

        // then
        verify(personRepository, times(1)).delete(byUuid);
    }

}
