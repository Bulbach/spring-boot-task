package ru.clevertec.ecl.configuration;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.clevertec.ecl.cache.AbstractCache;
import ru.clevertec.ecl.cache.impl.LFUCache;
import ru.clevertec.ecl.cache.impl.LRUCache;
import ru.clevertec.ecl.dto.requestDto.RequestDtoHouse;
import ru.clevertec.ecl.dto.requestDto.RequestDtoPerson;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;


@Slf4j
@Configuration
public class AppConfig {

    @Value("${spring.cache.algorithm}")
    private String CACHE_ALGORITHM;
    @Value("${spring.cache.max-size}")
    private int CAPACITY_KEY;
    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {

        PropertySourcesPlaceholderConfigurer propertyConfigurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        Properties yamlObject = Objects.requireNonNull(yaml.getObject(), "Could not load yml");
        propertyConfigurer.setProperties(yamlObject);
        return propertyConfigurer;
    }

    @Bean
    public AbstractCache<UUID, ResponseDtoHouse> houseCache() {
        return "LFU".equals(CACHE_ALGORITHM)
                ? new LFUCache<>(CAPACITY_KEY)
                : new LRUCache<>(CAPACITY_KEY);
    }
    @Bean
    public AbstractCache<UUID, ResponseDtoPerson> personCache() {
        return "LFU".equals(CACHE_ALGORITHM)
                ? new LFUCache<>(CAPACITY_KEY)
                : new LRUCache<>(CAPACITY_KEY);
    }

}
