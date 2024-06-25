package ru.maipomogator.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class BotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }

    @Bean
    RestClient restClient(@NonNull @Value("${baseurl:https://rufus20145.ru/api/v2}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
