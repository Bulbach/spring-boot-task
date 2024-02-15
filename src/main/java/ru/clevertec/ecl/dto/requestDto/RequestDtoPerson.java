package ru.clevertec.ecl.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Passport;
import ru.clevertec.ecl.entity.Person;

import java.time.LocalDateTime;

public record RequestDtoPerson
        (
        String uuid,
        String name,
        String surname,
        Person.Sex sex,
        LocalDateTime createDate,
        LocalDateTime updateDate,
        Passport passport,
        House house,
        boolean isOwner
)
{}
