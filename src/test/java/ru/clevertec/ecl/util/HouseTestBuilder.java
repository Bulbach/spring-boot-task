package ru.clevertec.ecl.util;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestConstructor;
import ru.clevertec.ecl.dto.requestDto.RequestDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Person;
import ru.clevertec.ecl.mapper.HouseMapper;
import ru.clevertec.ecl.mapper.HouseMapperImpl;
import ru.clevertec.ecl.mapper.PersonMapper;
import ru.clevertec.ecl.mapper.PersonMapperImpl;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class HouseTestBuilder {

    @Builder.Default()
    private Long id = 1l;

    @Builder.Default()
    private UUID uuid = UUID.fromString("b7d69c82-9833-4364-9c77-a0ecfc467c63");

    @Builder.Default()
    private Double area = 122.4;

    @Builder.Default()
    private String country = "Burundia";

    @Builder.Default()
    private String city = "Whole";

    @Builder.Default()
    private String street = "Garden";

    @Builder.Default()
    private String houseNumber = "13";

    @Builder.Default()
    private LocalDateTime createDate = LocalDateTime.of(2022, Month.MAY, 14, 13, 45);
//
//    @Builder.Default()
//    private Set<Person> residents = new HashSet<>();
//
//    @Builder.Default()
//    private List<Person> owners = new ArrayList<>();

    private final HouseMapper houseMapper = new HouseMapperImpl();
    private final PersonMapper personMapper = new PersonMapperImpl();

    public static List<Person> buildOwners() {
        List<Person> testOwnerPerson = new ArrayList<>();
        testOwnerPerson.add(PersonTestBuilder.builder().build().persons().get(0));
        testOwnerPerson.add(PersonTestBuilder.builder().build().persons().get(1));
        return testOwnerPerson;
    }

    public List<ResponseDtoPerson> buildDtoOwners(){
        return buildOwners().stream().map(personMapper::toDto).collect(Collectors.toList());
    }
    public static Set<Person> buildResidents() {
        Set<Person> testResidentsPerson = new HashSet<>();
        testResidentsPerson.add(PersonTestBuilder.builder().build().persons().get(1));
        testResidentsPerson.add(PersonTestBuilder.builder().build().persons().get(2));
        return testResidentsPerson;
    }
    public Set<ResponseDtoPerson> buildDtoResidents(){
        return buildResidents().stream().map(personMapper::toDto).collect(Collectors.toSet());
    }

    public List<Person> buildTenants(){
        List<Person> testTenantPerson = new ArrayList<>();
        testTenantPerson.add(PersonTestBuilder.builder().build().persons().get(0));
        testTenantPerson.add(PersonTestBuilder.builder().build().persons().get(2));
        return testTenantPerson;
    }
    public List<ResponseDtoPerson> buildDtoTenants(){
        return buildTenants().stream().map(personMapper::toDto).collect(Collectors.toList());
    }

    public static House testHouse() {
        House house = House.builder()
                .uuid(UUID.fromString("b7d69c82-9833-4364-9c77-a0ecfc467c63"))
                .area(345.2)
                .country("Burundia")
                .city("Grust")
                .street("Mad_street")
                .houseNumber("213")
                .createDate(LocalDateTime.now())
                .build();
        return house;
    }

    public RequestDtoHouse buildRequestDtoHouse() {
        return new RequestDtoHouse(uuid.toString(), area, country, city, street, houseNumber, createDate);
    }

    public RequestDtoHouse buildCreateRequestDtoHouse() {
        return new RequestDtoHouse(null, area, country, city, street, houseNumber, createDate);
    }

    public ResponseDtoHouse buildResponseDtoHouse() {
        return new ResponseDtoHouse(uuid.toString(), area, country, city, street, houseNumber, createDate);
    }

    public List<House> houses() {

        return List.of(
                House.builder()
                        .uuid(UUID.fromString("b7d69c82-9833-4364-9c77-a0ecfc467c63"))
                        .area(345.2)
                        .country("Burundia")
                        .city("Grust")
                        .street("Mad_street")
                        .houseNumber("213")
                        .createDate(LocalDateTime.now())
//                        .residents(new HashSet<>())
//                        .owners(new ArrayList<>())
                        .build(),
                House.builder()
                        .uuid(UUID.fromString("0d307e34-d7cb-4673-9ddd-92d64409995e"))
                        .area(212.2)
                        .country("Normand")
                        .city("Ga")
                        .street("shtrasse")
                        .houseNumber("111")
                        .createDate(LocalDateTime.now())
//                        .residents(new HashSet<>())
//                        .owners(new ArrayList<>())
                        .build(),
                House.builder()
                        .uuid(UUID.fromString("6d06634d-453d-4a8f-854b-04b1e32756a5"))
                        .area(212.2)
                        .country("Poland")
                        .city("Varshava")
                        .street("Pha")
                        .houseNumber("545")
                        .createDate(LocalDateTime.now())
//                        .residents(new HashSet<>())
//                        .owners(new ArrayList<>())
                        .build()

        );
    }

    public List<ResponseDtoHouse> dtoHouses() {
        return houses().stream().map(houseMapper::toDto).collect(Collectors.toList());
    }
}

