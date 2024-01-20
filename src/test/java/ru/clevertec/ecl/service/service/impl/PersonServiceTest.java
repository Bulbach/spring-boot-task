package ru.clevertec.ecl.service.service.impl;

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
import ru.clevertec.ecl.mapper.HouseMapper;
import ru.clevertec.ecl.mapper.PersonMapper;
import ru.clevertec.ecl.repository.impl.PersonRepositoryImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepositoryImpl personRepository;

    @Mock
    private PersonMapper personMapper;

    @Mock
    private HouseMapper houseMapper;

    @InjectMocks
    private PersonService personService;

    @Test
    void getAllPersons() {
        // given
        List<Person> mockPersons = Arrays.asList(new Person(), new Person());
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
        Collection<ResponseDtoPerson> result = personService.getAllPersons(10);

        // then
        assertEquals(2, result.size());
        verify(personRepository, times(1)).findAll();
        verify(personMapper, times(2)).toDto(any());
    }

    @Test
    void getById() {
        // given
        UUID personId = UUID.fromString("feda712b-54b8-4e9e-ba67-fbc5665c3cab");
        Person mockPerson = new Person();
        when(personRepository.findById(personId)).thenReturn(Optional.of(mockPerson));
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
        ResponseDtoPerson result = personService.getById(personId);

        // then
        assertNotNull(result);
        verify(personRepository, times(1)).findById(personId);
        verify(personMapper, times(1)).toDto(any());
    }

    @Test
    void getOwnedHouses() {
        // given
        UUID personId = UUID.fromString("feda712b-54b8-4e9e-ba67-fbc5665c3cab");
        List<House> mockOwnedHouses = new LinkedList<>(Arrays.asList(new House(), new House()));
        when(personRepository.getOwnedHouses(personId)).thenReturn(mockOwnedHouses);
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
        Collection<ResponseDtoHouse> result = personService.getOwnedHouses(personId);

        // then
        assertEquals(2, result.size());
        verify(personRepository, times(1)).getOwnedHouses(personId);
        verify(houseMapper, times(2)).toDto(any());
    }

    @Test
    void create() {
        // when
        RequestDtoPerson requestDtoPerson = new RequestDtoPerson(
                "feda712b-54b8-4e9e-ba67-fbc5665c3cab",
                "John",
                "Doe",
                Person.Sex.Male,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Passport("AB123", "456789"),
                new House(),
                false
        );
        Person mockPerson = new Person();
        when(personMapper.toModel(requestDtoPerson)).thenReturn(mockPerson);
        when(personRepository.create(mockPerson)).thenReturn(mockPerson);
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
        ResponseDtoPerson result = personService.create(requestDtoPerson);

        // then
        assertNotNull(result);
        verify(personMapper, times(1)).toModel(requestDtoPerson);
        verify(personRepository, times(1)).create(mockPerson);
        verify(personMapper, times(1)).toDto(mockPerson);
    }

        @Test
    void update() {
        // when
        UUID personId = UUID.fromString("feda712b-54b8-4e9e-ba67-fbc5665c3cab");
        RequestDtoPerson requestDtoPerson = new RequestDtoPerson(
                "feda712b-54b8-4e9e-ba67-fbc5665c3cab",
                "John",
                "Doe",
                Person.Sex.Male,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Passport("AB123", "456789"),
                new House(),
                true
        );
        Person existingPerson = new Person();
        existingPerson.setUuid(personId);
        existingPerson.setName("Alice");
        existingPerson.setSurname("Wonderland");
        existingPerson.setSex(Person.Sex.Female);
        existingPerson.setCreateDate(LocalDateTime.now().minusDays(1));
        existingPerson.setUpdateDate(LocalDateTime.now().minusDays(1));
        when(personRepository.findById(personId)).thenReturn(Optional.of(existingPerson));
        doNothing().when(personMapper).updateModel(requestDtoPerson, existingPerson);
        when(personRepository.update(existingPerson)).thenReturn(existingPerson);
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
        ResponseDtoPerson result = personService.update(personId, requestDtoPerson);

        // then
        assertNotNull(result);
        verify(personRepository, times(1)).findById(personId);
        verify(personMapper, times(1)).updateModel(requestDtoPerson, existingPerson);
        verify(personRepository, times(1)).update(existingPerson);
        verify(personMapper, times(1)).toDto(existingPerson);
    }
    @Test
    void delete() {
        // when
        UUID personId = UUID.fromString("feda712b-54b8-4e9e-ba67-fbc5665c3cab");

        // given
        personService.delete(personId);

        // then
        verify(personRepository, times(1)).delete(personId);
    }
}