package ru.clevertec.ecl.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import ru.clevertec.ecl.dto.requestDto.RequestDtoPerson;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Passport;
import ru.clevertec.ecl.entity.Person;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class PersonTestBuilder {

    @Builder.Default()
    private Long id = 1L;
    @Builder.Default()
    private UUID uuid = UUID.fromString("fd668dac-ad7f-4a24-87a7-c49b435f74bb");
    @Builder.Default()
    private String name = "Alex";
    @Builder.Default()
    private String surname = "Verezubov";
    @Builder.Default()
    private Person.Sex sex = Person.Sex.Male;
    @Builder.Default()
    private LocalDateTime createDate = LocalDateTime.now();
    @Builder.Default()
    private LocalDateTime updateDate = LocalDateTime.MAX;
    @Builder.Default()
    private Passport passport = new Passport("HB234", "2344t56");
    @Builder.Default()
    private House house = HouseTestBuilder.builder().build().houses().get(2);
    @Builder.Default()
    private Set<House> ownedHouses = new HashSet<>();

    public static Person buildPerson() {

        return Person.builder()
                .uuid(UUID.fromString("7ab1945c-81e0-4ab0-b41f-e776d13ac6a3"))
                .name("Volha")
                .surname("Appel")
                .sex(Person.Sex.Female)
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.MAX)
                .house(HouseTestBuilder.builder().build().houses().get(2))
                .passport(PersonTestBuilder.builder().build().passports().get(2))
                .build();
    }

    public RequestDtoPerson requestDtoPerson(House house) {

        Random random = new Random();
        String uuid = UUID.randomUUID().toString();
        List<Person> persons = persons();
        Person person = persons.get(random.nextInt(persons.size()));
        return new RequestDtoPerson(uuid,
                person.getName(),
                person.getSurname(),
                person.getSex(),
                person.getCreateDate(),
                person.getUpdateDate(),
                new Passport(uuid.substring(0, 5), uuid.substring(5, 10)),
                house,
                false);
    }

    public ResponseDtoPerson responseDtoPerson() {
        return new ResponseDtoPerson(uuid.toString(), name, surname, sex.toString(), createDate, updateDate, passport);
    }

    public enum Sex {
        Male, Female
    }

    public List<Person> persons() {
        return List.of(
                Person.builder()
                        .uuid(UUID.fromString("4c78be6d-1a6d-47bb-ae4a-0b63f2beccd0"))
                        .name("Burum")
                        .surname("Turum")
                        .sex(Person.Sex.Male)
                        .createDate(LocalDateTime.of(2022, Month.MAY, 14, 13, 45))
                        .updateDate(LocalDateTime.now())
                        .passport(passports().get(0))
                        .house(HouseTestBuilder.builder().build().houses().get(0))
                        .build(),
                Person.builder()
                        .uuid(UUID.fromString("201a14b7-3cb5-4b94-ada6-a09c6464536b"))
                        .name("Maria")
                        .surname("Volantio")
                        .sex(Person.Sex.Female)
                        .createDate(LocalDateTime.of(2023, Month.OCTOBER, 28, 20, 20))
                        .updateDate(LocalDateTime.now())
                        .passport(passports().get(1))
                        .house(HouseTestBuilder.builder().build().houses().get(1))
                        .build(),
                Person.builder()
                        .uuid(UUID.fromString("feda712b-54b8-4e9e-ba67-fbc5665c3cab"))
                        .name("Mark")
                        .surname("Cucumber")
                        .sex(Person.Sex.Male)
                        .createDate(LocalDateTime.of(2023, Month.AUGUST, 20, 15, 15))
                        .updateDate(LocalDateTime.now())
                        .passport(passports().get(2))
                        .house(HouseTestBuilder.builder().build().houses().get(2))
                        .build()
        );
    }

    public List<Passport> passports() {
        return List.of(
                new Passport("DF345", "989zc87"),
                new Passport("BG564", "342x2z4"),
                new Passport("NH876", "356lkj7")
        );
    }
}
