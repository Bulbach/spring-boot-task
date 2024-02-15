package ru.clevertec.ecl.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Person;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HouseJpaRepository extends JpaRepository<House, UUID> {
    Optional<House> findByUuid(UUID uuid);

    /**
     * Получение всех Person, которые когда-либо проживали в доме с указанным UUID
     *
     * @param houseId UUID дома
     * @return Список Person
     */
    @Query("SELECT p FROM Person p " +
            "JOIN p.houseHistories hh " +
            "JOIN hh.house h " +
            "WHERE h.uuid = :houseId " +
            "AND hh.type = 'TENANT'")
    List<Person> findTenantsByHouseId(@Param("houseId") UUID houseId);

    /**
     * Получение всех Person, которые когда-либо владели домом с указанным UUID
     *
     * @param houseId UUID дома
     * @return Список Person
     */
    @Query("SELECT p FROM Person p " +
            "JOIN p.houseHistories hh " +
            "JOIN hh.house h " +
            "WHERE h.uuid = :houseId " +
            "AND hh.type = 'OWNER'")
    List<Person> findOwnersByHouseId(@Param("houseId") UUID houseId);

}
