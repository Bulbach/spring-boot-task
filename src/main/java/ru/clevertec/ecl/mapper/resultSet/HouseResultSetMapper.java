package ru.clevertec.ecl.mapper.resultSet;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.entity.House;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class HouseResultSetMapper implements RowMapper<House> {

    @Override
    public House mapRow(ResultSet rs, int rowNum) throws SQLException {
        House house = new House();
        house.setUuid(UUID.fromString(rs.getString("uuid")));
        house.setArea(rs.getDouble("area"));
        house.setCountry(rs.getString("country"));
        house.setCity(rs.getString("city"));
        house.setStreet(rs.getString("street"));
        house.setHouseNumber(rs.getString("houseNumber"));
        house.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
        return house;
    }
}
