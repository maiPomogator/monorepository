package ru.maipomogator.bot.clients;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import ru.maipomogator.bot.model.Lesson;
import ru.maipomogator.bot.model.Professor;

@Component
public class ProfessorRestClient {
    private static final ParameterizedTypeReference<List<Professor>> PROFS_LIST_TR = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<Lesson>> LESSONS_LIST_TR = new ParameterizedTypeReference<>() {};

    private final RestClient restClient;

    public ProfessorRestClient(@Value("${baseurl:https://rufus20145.ru}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public List<Professor> getAll() {
        List<Professor> professors = restClient.get().uri("/mai/professors").retrieve().body(PROFS_LIST_TR);
        professors.removeIf(p -> p.siteId().equals(new UUID(0, 0)));
        return professors;
    }

    public List<Lesson> getLessons(Long id) {
        return restClient.get().uri("/mai/professors/{id}/lessons", id).retrieve().body(LESSONS_LIST_TR);
    }
}
