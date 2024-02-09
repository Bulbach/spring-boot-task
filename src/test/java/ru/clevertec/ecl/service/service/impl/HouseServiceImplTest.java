package ru.clevertec.ecl.service.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.ecl.dto.requestDto.RequestDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Passport;
import ru.clevertec.ecl.entity.Person;
import ru.clevertec.ecl.mapper.HouseMapper;
import ru.clevertec.ecl.mapper.PersonMapper;
import ru.clevertec.ecl.repository.impl.HouseRepositoryImpl;
import ru.clevertec.ecl.repository.jpa.PersonJpaRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class HouseServiceImplTest {

    @Mock
    private HouseRepositoryImpl houseRepository;
    @Mock
    private PersonJpaRepository personJpaRepository;

    @Mock
    private HouseMapper houseMapper;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private HouseServiceImpl houseServiceImpl;

    @BeforeEach
    void setUp() {
        UUID uuid1 = UUID.fromString("4c78be6d-1a6d-47bb-ae4a-0b63f2beccd0");
        UUID uuid2 = UUID.fromString("feda712b-54b8-4e9e-ba67-fbc5665c3cab");
        UUID uuid3 = UUID.fromString("feda712b-54b8-4e9e-ba67-fbc5665c3cab");

    }

    @Test
    void getAll() {

        // given
        List<House> mockHouses = Arrays.asList( House.builder().build(), House.builder().build());
        when(houseRepository.findAll()).thenReturn(mockHouses);
        when(houseMapper.toDto(any()))
                .thenReturn(new ResponseDtoHouse("4c78be6d-1a6d-47bb-ae4a-0b63f2beccd0",
                        150.3,
                        "Country",
                        "City",
                        "Street",
                        "13",
                        LocalDateTime.now()
                ));

        // when
        Collection<ResponseDtoHouse> result = houseServiceImpl.getAll(2);

        // then
        assertEquals(2, result.size());
        verify(houseRepository, times(1)).findAll();
        verify(houseMapper, times(2)).toDto(any());
    }

    @Test
    void getById() {

        // given
        UUID houseId = UUID.fromString("4c78be6d-1a6d-47bb-ae4a-0b63f2beccd0");
        House mockHouse = House.builder().build();
        when(houseRepository.findById(houseId)).thenReturn(Optional.of(mockHouse));
        when(houseMapper.toDto(any()))
                .thenReturn(new ResponseDtoHouse("4c78be6d-1a6d-47bb-ae4a-0b63f2beccd0",
                        150.3,
                        "Country",
                        "City",
                        "Street",
                        "13",
                        LocalDateTime.now()
                ));

        // when
        ResponseDtoHouse result = houseServiceImpl.getById(houseId);

        // then
        assertNotNull(result);
        verify(houseRepository, times(1)).findById(houseId);
        verify(houseMapper, times(1)).toDto(any());
    }

    @Test
    void getResidents() {
        // given
        UUID houseId = UUID.randomUUID();
        List<Person> mockResidents = Arrays.asList( Person.builder().build(), Person.builder().build());
        when(houseRepository.getResidents(houseId)).thenReturn(mockResidents);
        when(personMapper.toDto(any())).thenReturn(new ResponseDtoPerson(
                "feda712b-54b8-4e9e-ba67-fbc5665c3cab",
                "Alex",
                "Big",
                "Male",
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Passport("CD456", "789012")

        ));

        // when
        Collection<ResponseDtoPerson> result = houseServiceImpl.getResidents(houseId);

        // then
        assertEquals(2, result.size());
        verify(houseRepository, times(1)).getResidents(houseId);
        verify(personMapper, times(2)).toDto(any());
    }

    @Test
    void create() {
        // when
        RequestDtoHouse requestDtoHouse = new RequestDtoHouse("4c78be6d-1a6d-47bb-ae4a-0b63f2beccd0",
                150.3,
                "Country",
                "City",
                "Street",
                "13",
                LocalDateTime.now());
        House mockHouse = House.builder().build();
        when(houseMapper.toModel(requestDtoHouse)).thenReturn(mockHouse);
        when(houseRepository.create(mockHouse)).thenReturn(mockHouse);
        when(houseMapper.toDto(mockHouse)).thenReturn(new ResponseDtoHouse("4c78be6d-1a6d-47bb-ae4a-0b63f2beccd0",
                150.3,
                "Country",
                "City",
                "Street",
                "13",
                LocalDateTime.now()));

        // given
        ResponseDtoHouse result = houseServiceImpl.create(requestDtoHouse);

        // then
        assertNotNull(result);
        verify(houseMapper, times(1)).toModel(requestDtoHouse);
        verify(houseRepository, times(1)).create(mockHouse);
        verify(houseMapper, times(1)).toDto(mockHouse);
    }

    @Test
    void update() {
        // when
        UUID houseId = UUID.randomUUID();
        RequestDtoHouse requestDtoHouse = new RequestDtoHouse("4c78be6d-1a6d-47bb-ae4a-0b63f2beccd0",
                150.3,
                "Germany",
                "City",
                "Street",
                "15",
                LocalDateTime.now());
        House existingHouse = House.builder().build();
        existingHouse.setUuid(UUID.fromString("4c78be6d-1a6d-47bb-ae4a-0b63f2beccd0"));
        existingHouse.setArea(140.1);
        existingHouse.setCountry("Country");
        existingHouse.setCity("City");
        existingHouse.setStreet("Street");
        existingHouse.setHouseNumber("15");
        when(houseRepository.findById(houseId)).thenReturn(Optional.of(existingHouse));
        doNothing().when(houseMapper).updateModel(requestDtoHouse, existingHouse);
        when(houseRepository.update(existingHouse)).thenReturn(existingHouse);
        when(houseMapper.toDto(existingHouse)).thenReturn(new ResponseDtoHouse("4c78be6d-1a6d-47bb-ae4a-0b63f2beccd0",
                150.3,
                "Germany",
                "City",
                "Street",
                "15",
                LocalDateTime.now()));

        // given
        ResponseDtoHouse result = houseServiceImpl.update(houseId, requestDtoHouse);

        // then
        assertNotNull(result);
        verify(houseRepository, times(1)).findById(houseId);
        verify(houseMapper, times(1)).updateModel(requestDtoHouse, existingHouse);
        verify(houseRepository, times(1)).update(existingHouse);
        verify(houseMapper, times(1)).toDto(existingHouse);
    }

    @Test
    void delete() {
        // when
        UUID houseId = UUID.fromString("feda712b-54b8-4e9e-ba67-fbc5665c3cab");

        // given
        houseServiceImpl.delete(houseId);

        // then
        verify(houseRepository, times(1)).delete(houseId);
    }
}
