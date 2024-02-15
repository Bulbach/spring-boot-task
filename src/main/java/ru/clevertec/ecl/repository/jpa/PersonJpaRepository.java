package ru.clevertec.ecl.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Passport;
import ru.clevertec.ecl.entity.Person;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PersonJpaRepository extends JpaRepository<Person, UUID> {

    Optional<Person> findByUuid(UUID uuid);

    /**
     * Получение всех House, где когда-либо проживал Person с указанным UUID
     *
     * @param personId UUID человека
     * @return Список House
     */
    @Query("SELECT h FROM House h " +
            "JOIN h.houseHistories hh " +
            "JOIN hh.person p " +
            "WHERE p.uuid = :personId " +
            "AND hh.type = 'TENANT'")
    List<House> findHousesByPersonId(@Param("personId") UUID personId);

    /**
     * Получение всех House, которыми когда-либо владел Person с указанным UUID
     *
     * @param personId UUID человека
     * @return Список House
     */
    @Query("SELECT h FROM House h " +
            "JOIN h.houseHistories hh " +
            "JOIN hh.person p " +
            "WHERE p.uuid = :personId " +
            "AND hh.type = 'OWNER'")
    List<House> findOwnedHousesByPersonId(@Param("personId") UUID personId);

    @Modifying
    @Query(value = "INSERT INTO house_owners (house_id, person_id) VALUES (:houseId, :personId)", nativeQuery = true)
    void addOwnerToHouse(@Param("houseId") Long houseId, @Param("personId") Long personId);

    Optional<Long> countPersonByPassport(@Param("passport") Passport passport);
}


