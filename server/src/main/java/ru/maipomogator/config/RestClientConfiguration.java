package ru.maipomogator.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JettyClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {
    @Bean
    RestClient restClient(List<HttpMessageConverter<?>> messageConverters) {
        return RestClient.builder().baseUrl("https://public.mai.ru/schedule/data/")
                .requestFactory(new JettyClientHttpRequestFactory())
                .messageConverters(messageConverters)
                .build();
    }
}
