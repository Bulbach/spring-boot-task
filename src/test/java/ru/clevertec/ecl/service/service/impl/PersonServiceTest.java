package ru.clevertec.ecl.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;
import ru.clevertec.ecl.PostgresSqlContainerInitialization;
import ru.clevertec.ecl.dto.requestDto.RequestDtoPerson;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Passport;
import ru.clevertec.ecl.entity.Person;
import ru.clevertec.ecl.mapper.HouseMapper;
import ru.clevertec.ecl.mapper.PersonMapper;
import ru.clevertec.ecl.repository.jpa.HouseJpaRepository;
import ru.clevertec.ecl.repository.jpa.PersonJpaRepository;

import java.util.concurrent.ExecutorService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class PersonServiceTest extends PostgresSqlContainerInitialization {

    private final PersonService personService;
//    @MockBean
    private PersonJpaRepository personRepository;
//    @MockBean
    private HouseJpaRepository houseJpaRepository;
//    @MockBean
    private PersonMapper personMapper;

//    @MockBean
    private HouseMapper houseMapper;
//    @MockBean
//    @Autowired
    private SessionFactory sessionFactory;


    //    @Test
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

    //    @Test
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

    //    @Test
    void getOwnedHouses() {
        // given
        UUID personId = UUID.fromString("feda712b-54b8-4e9e-ba67-fbc5665c3cab");
        List<House> mockOwnedHouses = new ArrayList<>(Arrays.asList(new House(), new House()));
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
        Collection<ResponseDtoHouse> result = personService.getOwnedHousesByPersonId(personId);

        // then
        assertEquals(2, result.size());
        verify(personRepository, times(1)).findOwnedHousesByPersonId(personId);
        verify(houseMapper, times(2)).toDto(any());
    }

    //    @Test
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
        ResponseDtoPerson result = personService.create(requestDtoPerson);

        // then
        assertNotNull(result);
        verify(personMapper, times(1)).toModel(requestDtoPerson);
        verify(personRepository, times(1)).save(mockPerson);
        verify(personMapper, times(1)).toDto(mockPerson);
    }

    //    @Test
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
        ResponseDtoPerson result = personService.update(personId, requestDtoPerson);

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
        Person byId = personRepository.findByUuid(personId);
        // given
        personService.delete(personId);

        // then
        verify(personRepository, times(1)).delete(byId);
    }

    private static ExecutorService executorService;
    private static int threadCount = 6;

    @Test
    void testCacheInMultiThreadEnvironment() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // Выполнение GET, POST, PUT, DELETE запросов к сервисному слою
                    // и проверка результатов
                    // Например, personService.getById(UUID.randomUUID());
                    // ...
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // Проверка состояния кэша после параллельных запросов
        // Например, проверка, что кэш был использован и не был изменен
        // ...
    }
}
