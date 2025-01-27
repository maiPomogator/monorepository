package ru.maipomogator.domain.mai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Component
@ConfigurationProperties(prefix = "updater")
@Accessors(fluent = true)
@Getter
@Setter
public class MaiUpdaterConfig {
    /**
     * Включать ли группы, которые отсутствуют в ответе API МАИ.
     */
    private boolean includeMissingGroups = true;
}
