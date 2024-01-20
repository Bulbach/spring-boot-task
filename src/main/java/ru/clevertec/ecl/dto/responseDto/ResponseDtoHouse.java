package ru.clevertec.ecl.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ResponseDtoHouse(
        String uuid,
        Double area,
        String country,
        String city,
        String street,
        String houseNumber,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        LocalDateTime createDate
)
{}
