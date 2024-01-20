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

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;


@Slf4j
@Configuration
@ComponentScan(basePackages = "ru.clevertec.ecl")
public class AppConfig {

    @Value("${spring.database.driver-class-name}")
    private String DATABASE_DRIVER;
    @Value("${spring.database.url}")
    private String DATABASE_URL;
    @Value("${spring.database.username}")
    private String DATABASE_USER;
    @Value("${spring.database.password}")
    private String DATABASE_PASSWORD;

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
    public DataSource getDataSource() throws SQLException, PropertyVetoException {

        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(DATABASE_DRIVER);
        hikariDataSource.setJdbcUrl(DATABASE_URL);
        hikariDataSource.setUsername(DATABASE_USER);
        hikariDataSource.setPassword(DATABASE_PASSWORD);
        hikariDataSource.setMinimumIdle(3);
        hikariDataSource.setMaximumPoolSize(10);

        log.info("create database");
        return hikariDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws PropertyVetoException, SQLException {

        return new JdbcTemplate(getDataSource());
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:/changelog.yaml");

        return liquibase;
    }

}
