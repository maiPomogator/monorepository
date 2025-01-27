package ru.maipomogator.updaters.mai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Component
@ConfigurationProperties(prefix = "updaters.mai")
@Accessors(fluent = true)
@Getter
@Setter
public class MaiConfig {
    /**
     * Включать ли группы, которые отсутствуют в ответе API МАИ.
     */
    private boolean includeMissingGroups = true;
}
