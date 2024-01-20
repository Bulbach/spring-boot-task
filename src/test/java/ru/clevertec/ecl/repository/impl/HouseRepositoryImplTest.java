//package ru.clevertec.ecl.repository.impl;
//
//import lombok.RequiredArgsConstructor;
//import org.hibernate.SessionFactory;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.containers.PostgreSQLContainer;
//import ru.clevertec.ecl.configuration.TestDatabaseConfig;
//import ru.clevertec.ecl.entity.House;
//
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@Testcontainers
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestDatabaseConfig.class)
//@ActiveProfiles("test")
//public class HouseRepositoryImplTest {
//
//    @Container
//    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.2");
//
////    @DynamicPropertySource
////    static void postgresProperties(DynamicPropertyRegistry registry) {
////        registry.add("spring.datasource.url", postgres::getJdbcUrl);
////        registry.add("spring.datasource.username", postgres::getUsername);
////        registry.add("spring.datasource.password", postgres::getPassword);
////    }
//
//    @Autowired
//    private HouseRepositoryImpl houseRepository;
//
//    @Test
//    void findById() {
//    }
//
//    @Test
//    void getResidents() {
//    }
//
//    @Test
//    void findAll() {
//    }
//
//    @Test
//    void create() {
//    }
//
//    @Test
//    void update() {
//    }
//
//    @Test
//    void testDelete() {
//        // Test the repository method
//        House house = new House();
//        house.setCountry("TestCountry");
//        house.setCity("TestCity");
//        house.setStreet("TestStreet");
//        house.setHouseNumber("TestHouseNumber");
//
//        House createdHouse = houseRepository.create(house);
//
//        UUID houseUuid = createdHouse.getUuid();
//
//        // Delete the house
//        houseRepository.delete(houseUuid);
//
//        // Verify that the house is deleted
//        assertNull(houseRepository.findById(houseUuid).orElse(null));
//    }
//}