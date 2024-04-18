package ru.maipomogator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JettyClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {
    @Bean
    RestClient restClient() {
        return RestClient.builder().baseUrl("https://public.mai.ru/schedule/data/")
                .requestFactory(new JettyClientHttpRequestFactory())
                .build();
    }
}
