package ru.maipomogator.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import ru.maipomogator.model.Group;
import ru.maipomogator.model.Lesson;
import ru.maipomogator.parser.adapters.GroupListAdapter;
import ru.maipomogator.parser.adapters.ParsedGroupAdapter;

@Configuration
public class GsonConfiguration {
    @Bean
    Gson gson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(new TypeToken<List<Group>>() {}.getType(), new GroupListAdapter())
                .registerTypeAdapter(new TypeToken<List<Lesson>>() {}.getType(), new ParsedGroupAdapter());
        return builder.create();
    }
}
