package ru.clevertec.ecl.dto.requestDto;

import java.time.LocalDateTime;

public record RequestDtoHouse(
        String uuid,
        Double area,
        String country,
        String city,
        String street,
        String houseNumber,
        LocalDateTime createDate
)
{}
