package ru.maipomogator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = { "ru.maipomogator" })
@EntityScan(basePackages = { "ru.maipomogator.model" })
@EnableJpaRepositories(basePackages = { "ru.maipomogator.repo" })
@ConfigurationPropertiesScan(basePackages = { "ru.maipomogator" })
@EnableScheduling
@EnableCaching
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}