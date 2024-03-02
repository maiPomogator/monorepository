package ru.maipomogator.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "ru.maipomogator" })
@EntityScan(basePackages = { "ru.maipomogator.model" })
@EnableJpaRepositories(basePackages = { "ru.maipomogator.repo" })
public class RestApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }
}