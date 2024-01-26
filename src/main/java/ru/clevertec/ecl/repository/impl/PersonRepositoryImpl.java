package ru.clevertec.ecl.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.dto.requestDto.RequestDtoPerson;
import ru.clevertec.ecl.entity.House;
import ru.clevertec.ecl.entity.Person;
import ru.clevertec.ecl.mapper.PersonMapper;
import ru.clevertec.ecl.repository.GenericRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Repository
public class PersonRepositoryImpl implements GenericRepository<Person, UUID> {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private HouseRepositoryImpl houseRepository;

    @Autowired
    private PersonMapper personMapper;

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

    public Set<House> getOwnedHouses(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Person> personQuery = session.createQuery("SELECT p FROM Person p WHERE p.uuid = :uuid", Person.class);
            Person person = personQuery.setParameter("uuid", id).uniqueResult();

            String hql = "SELECT DISTINCT h FROM House h JOIN h.owners o WHERE o = :person";
            Query<House> query = session.createQuery(hql, House.class);
            query.setParameter("person", person);

            // Используйте HashSet для создания множества из результата запроса
            List<House> houseList = query.getResultList();
            return new HashSet<>(houseList);
        }
    }

    @Override
    public Person create(Person person) {
        House house;
        if (!isPersonUniqueByPassport(person)) {
            throw new RuntimeException("Person with the same details already exists");
        }

        person.setUuid(UUID.randomUUID());
        person.setCreateDate(LocalDateTime.now());
        person.setUpdateDate(LocalDateTime.now());

        Optional<House> houseById = houseRepository.findById(person.getHouse().getUuid());
        if (houseById.isPresent()) {
            house = houseById.get();
            person.setHouse(house);
        } else {
            throw new RuntimeException("House with uuid = " + person.getHouse().getUuid() + " is not exists");
        }
        Set<House> ownedHouses = getOwnedHouses(person.getUuid());
        if (person.isOwner() && !ownedHouses.contains(house)) {
            ownedHouses.add(house);
        } else {
            person.setHouse(house);
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
            person.setUpdateDate(LocalDateTime.now());
            return session.merge(person);
        } catch (Exception e) {
            log.error("Failed to update person", e);
            throw new RuntimeException("Failed to update person", e);
        }

    }

    public Optional<House> getHouse(UUID houseUuid) {
        return houseRepository.findById(houseUuid);

    }


    public Person update(RequestDtoPerson personDto) {
        House houseWillChange;
        try (Session session = sessionFactory.openSession()) {
//            Person person = findById(UUID.fromString(personDto.uuid()))
//                    .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + personDto.uuid()));

            Person person = session.get(Person.class, 25);
//            personMapper.updateModel(personDto, person);
//            Optional<House> houseById = houseRepository.findById(person.getHouse().getUuid());
            House house = session.get(House.class, 1);

            person.addHouse(house);
//            if (houseById.isPresent()) {
//                houseWillChange = houseById.get();
//                person.setHouse(houseWillChange);
//            } else {
//                throw new RuntimeException("House with uuid = " + person.getHouse().getUuid() + " is not exists");
//            }
//
//             Получаем список владений домов в рамках той же сессии
//            List<House> ownedHouses = getOwnedHouses(person.getUuid());
//            if (person.isOwner() && !ownedHouses.contains(houseWillChange)) {
//                ownedHouses.add(houseWillChange);
//            }
//            person.setOwnedHouses(ownedHouses);
//            person.setUpdateDate(LocalDateTime.now());
//
            return session.merge(person);
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
