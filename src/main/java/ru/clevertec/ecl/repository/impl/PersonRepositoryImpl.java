package ru.clevertec.ecl.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Person;
import ru.clevertec.ecl.exception.PersonNotFoundException;
import ru.clevertec.ecl.repository.GenericRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class PersonRepositoryImpl implements GenericRepository<Person, UUID> {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private HouseRepositoryImpl houseRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Person> findAll() {

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT p FROM Person p", Person.class).getResultList();
        }
    }

    @Override
    public Optional<Person> findById(UUID id) {

        try (Session session = sessionFactory.openSession()) {
            Query<Person> query = session.createQuery("SELECT p FROM Person p WHERE p.uuid = :uuid", Person.class);
            Person person = query.setParameter("uuid", id).uniqueResult();
            return Optional.ofNullable(person);
        }
    }

    public List<House> getOwnedHouses(UUID id) {

        try (Session session = sessionFactory.openSession()) {
            Query<Person> personQuery = session.createQuery("SELECT p FROM Person p WHERE p.uuid = :uuid", Person.class);
            Person person = personQuery.setParameter("uuid", id).uniqueResult();
            String hql = "SELECT DISTINCT h FROM House h JOIN h.owners o WHERE o = :person";
            Query<House> query = session.createQuery(hql, House.class);
            query.setParameter("person", person);
            return query.getResultList();
        }
    }

    @Override
    public Person create(Person person) {
        if (!isPersonUniqueByPassport(person)) {
            throw new RuntimeException("Person with the same details already exists");
        }

        person.setUuid(UUID.randomUUID());
        person.setCreateDate(LocalDateTime.now());
        person.setUpdateDate(LocalDateTime.now());

        Optional<House> houseById = houseRepository.findById(person.getHouse().getUuid());
        if (houseById.isPresent()) {
            House house = houseById.get();
            person.setHouse(house);
            if (person.isOwner()) {
                house.getOwners().add(person);
            }
        } else {
            throw new RuntimeException("House with uuid = " + person.getHouse().getUuid() + " is not exists");
        }

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(person);
            session.getTransaction().commit();
            return person;
        } catch (Exception e) {
            log.error("Failed to create person", e);
            throw new RuntimeException("Failed to create person", e);
        }
    }

    @Override
    public Person update(Person person) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            person.setUpdateDate(LocalDateTime.now());
            Person updatedPerson = session.merge(person);
            session.getTransaction().commit();
            return updatedPerson;
        } catch (Exception e) {
            log.error("Failed to update person", e);
            throw new RuntimeException("Failed to update person", e);
        }
    }

    @Override
    public void delete(UUID uuid) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session.createQuery("DELETE FROM Person p WHERE p.uuid = :uuid");
            query.setParameter("uuid", uuid);
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("Failed to delete person", e);
            throw new RuntimeException("Failed to delete person", e);
        }
    }


    public void deleteTemplate(UUID uuid) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session.createQuery("DELETE FROM Person p WHERE p.uuid = :uuid");
            query.setParameter("uuid", uuid);
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("Failed to delete person", e);
            throw new RuntimeException("Failed to delete person", e);
        }
    }

    private boolean isPersonUniqueByPassport(Person person) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(p) FROM Person p WHERE p.passport = :passport",
                    Long.class
            );

            Long count = query
                    .setParameter("passport", person.getPassport())
                    .uniqueResult();

            return count == 0; // Если count равен 0, то такой человек уникален
        } catch (Exception e) {
            log.error("Failed to check person uniqueness by passport", e);
            throw new RuntimeException("Failed to check person uniqueness by passport", e);
        }
    }
}
