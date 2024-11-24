package ru.maipomogator.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import ru.maipomogator.bot.config.ApiConfig;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = { "ru.maipomogator.bot.config" })
public class BotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }

    @Bean
    RestClient restClient(ApiConfig apiConfig) {
        return RestClient.builder().baseUrl(apiConfig.getUrl()).build();
    }
}
