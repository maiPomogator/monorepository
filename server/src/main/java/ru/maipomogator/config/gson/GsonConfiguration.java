package ru.maipomogator.config.gson;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
public class GsonConfiguration {
    @Bean
    Gson gson(List<TypeableAdapter> adapters) {
        log.debug("Creating gson with {} adapters.", adapters.size());
        GsonBuilder builder = new GsonBuilder();
        adapters.forEach(a -> builder.registerTypeAdapter(a.getTargetType(), a));
        Converters.registerAll(builder); // Register JavaTime converters
        return builder.create();
    }
}
