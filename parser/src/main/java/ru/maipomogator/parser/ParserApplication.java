package ru.maipomogator.parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = { "ru.maipomogator" })
@EntityScan(basePackages = { "ru.maipomogator.model" })
@EnableJpaRepositories(basePackages = { "ru.maipomogator.repo" })
@EnableScheduling
public class ParserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParserApplication.class, args);
    }
}
