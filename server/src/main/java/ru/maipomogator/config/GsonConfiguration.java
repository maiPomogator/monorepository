package ru.maipomogator.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.config.gson.adapters.GsonAdapter;

@Log4j2
@Configuration
public class GsonConfiguration {
    @Bean
    Gson gson(List<GsonAdapter> adapters) {
        log.debug("Creating gson with {} adapters.", adapters.size());
        GsonBuilder builder = new GsonBuilder();
        adapters.forEach(a -> builder.registerTypeAdapter(a.getType(), a));
        return builder.create();
    }
}
