package ru.maipomogator.bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Информация для подключения к API с данными расписания МАИ
 */
@ConfigurationProperties(prefix = "api")
@Getter
@RequiredArgsConstructor
public class ApiConfig {
    private final String url;
}
