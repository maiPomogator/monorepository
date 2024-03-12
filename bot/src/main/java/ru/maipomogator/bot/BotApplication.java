package ru.maipomogator.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class BotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }

    @Bean
    public RestClient restClient(@Value("${baseurl:https://rufus20145.ru}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
