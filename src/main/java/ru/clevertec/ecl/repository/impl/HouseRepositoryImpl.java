package ru.clevertec.ecl.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Person;
import ru.clevertec.ecl.repository.GenericRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class HouseRepositoryImpl implements GenericRepository<House, UUID> {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Optional<House> findById(UUID uuid) {
        try (Session session = sessionFactory.openSession()) {
            Query<House> query =
                    session.createQuery("SELECT h FROM House h WHERE h.uuid  = :uuid", House.class);
            House house1 = query.setParameter("uuid", uuid).uniqueResult();

            return Optional.ofNullable(house1);
        }
    }
    public List<Person> getResidents(UUID uuid) {
        try (Session session = sessionFactory.openSession()) {
            Query<House> houseQuery =
                    session.createQuery("SELECT h FROM House h WHERE h.uuid  = :uuid", House.class);
            House house = houseQuery.setParameter("uuid", uuid).uniqueResult();
            String hql = "SELECT DISTINCT p FROM Person p JOIN p.house h WHERE h.id = :houseId";
            Query<Person> query = session.createQuery(hql, Person.class);
            query.setParameter("houseId", house.getId());
            return query.getResultList();
        }
    }

    @Override
    public List<House> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT h FROM House h", House.class).getResultList();
        }
    }


    @Override
    public House create(House house) {
        if (!isHouseUnique(house.getCountry(), house.getCity(), house.getStreet(), house.getHouseNumber())) {
            throw new RuntimeException("House with the same details already exists");
        }
        house.setUuid(UUID.randomUUID());
        house.setCreateDate(LocalDateTime.now());

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(house);
            session.getTransaction().commit();
            return house;
        } catch (Exception e) {
            log.error("Failed to create house", e);
            throw new RuntimeException("Failed to create house", e);
        }
    }

    @Override
    public House update(House house) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            House updatedHouse = (House) session.merge(house);
            session.getTransaction().commit();
            return updatedHouse;
        } catch (Exception e) {
            log.error("Failed to update house", e);
            throw new RuntimeException("Failed to update house", e);
        }
    }

    @Override
    public void delete(UUID uuid) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session.createQuery("DELETE FROM House h WHERE h.uuid = :uuid");
            query.setParameter("uuid", uuid);
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("Failed to delete house", e);
            throw new RuntimeException("Failed to delete house", e);
        }
    }


    private boolean isHouseUnique(String country, String city, String street, String houseNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(h) FROM House h " +
                            "WHERE h.country = :country " +
                            "AND h.city = :city " +
                            "AND h.street = :street " +
                            "AND h.houseNumber = :houseNumber",
                    Long.class
            );

            Long count = query
                    .setParameter("country", country)
                    .setParameter("city", city)
                    .setParameter("street", street)
                    .setParameter("houseNumber", houseNumber)
                    .uniqueResult();

            return count == 0; // Если count равен 0, то такое сочетание уникально
        } catch (Exception e) {
            log.error("Failed to check house uniqueness", e);
            throw new RuntimeException("Failed to check house uniqueness", e);
        }
    }
}
