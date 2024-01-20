package ru.clevertec.ecl.mapper.resultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Passport;
import ru.clevertec.ecl.entity.Person;
import ru.clevertec.ecl.repository.impl.HouseRepositoryImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Component
public class PersonResultSetMapper implements RowMapper<Person> {

    @Autowired
    private HouseRepositoryImpl houseRepository;

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {

        Person person = new Person();
        person.setId(rs.getLong("id"));
        person.setUuid(UUID.fromString(rs.getString("uuid")));
        person.setName(rs.getString("name"));
        person.setSurname(rs.getString("surname"));
        person.setSex(Person.Sex.valueOf(rs.getString("sex")));
        person.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
        person.setUpdateDate(rs.getTimestamp("update_date").toLocalDateTime());

        Passport passport = new Passport();
        passport.setPassportSeries(rs.getString("passportSeries"));
        passport.setPassportNumber(rs.getString("passportNumber"));
        person.setPassport(passport);

        UUID houseId = UUID.fromString(rs.getString("id"));
        if (houseId != null) {
            Optional<House> house = houseRepository.findById(houseId);
            house.ifPresent(person::setHouse);
        }

        return person;
    }
}
