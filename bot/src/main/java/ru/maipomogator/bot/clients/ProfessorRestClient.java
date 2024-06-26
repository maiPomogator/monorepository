package ru.maipomogator.bot.clients;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.bot.model.Professor;

@Component
@RequiredArgsConstructor
public class ProfessorRestClient {
    @NonNull
    private static final ParameterizedTypeReference<List<Professor>> PROFS_LIST_TR = new ParameterizedTypeReference<>() {};

    private final RestClient restClient;

    public List<Professor> findAll() {
        List<Professor> professors = restClient.get().uri("/mai/professors").retrieve().body(PROFS_LIST_TR);
        if (professors == null) {
            return Collections.emptyList();
        }
        return professors;
    }

    public List<Professor> findByFio(String fio) {
        List<Professor> professors = restClient.get().uri("/mai/professors?fio={fio}", fio).retrieve()
                .body(PROFS_LIST_TR);
        if (professors == null) {
            return Collections.emptyList();
        }
        return professors;
    }

    public Professor findById(Long id) {
        return restClient.get().uri("/mai/professors/{id}", id).retrieve().body(Professor.class);
    }
}
