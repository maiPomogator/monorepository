package ru.maipomogator.bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Информация для подключения к Telegram Bot API
 */
@ConfigurationProperties(prefix = "bot")
@Getter
@RequiredArgsConstructor
public class BotConfig {
    private final String token;
}
