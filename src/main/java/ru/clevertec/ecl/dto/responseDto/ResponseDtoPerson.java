package ru.clevertec.ecl.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.clevertec.ecl.entity.Passport;

import java.time.LocalDateTime;

public record ResponseDtoPerson(
        String uuid,
        String name,
        String surname,
        String sex,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        LocalDateTime createDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        LocalDateTime updateDate,
        Passport passport
        )
{}
