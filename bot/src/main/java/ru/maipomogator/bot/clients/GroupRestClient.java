package ru.maipomogator.bot.clients;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.bot.model.Group;

@Component
@RequiredArgsConstructor
public class GroupRestClient {

    @NonNull
    private static final ParameterizedTypeReference<List<Group>> GROUPS_LIST_TR = new ParameterizedTypeReference<>() {};
    private final RestClient restClient;

    public List<Group> findByName(String name) {
        return restClient.get().uri("/mai/groups?name={name}", name).retrieve().body(GROUPS_LIST_TR);
    }

    public Group findById(Long id) {
        return restClient.get().uri("/mai/groups/{id}", id).retrieve().body(Group.class);
    }

    public List<Group> findByCourseAndFaculty(int course, int faculty) {
        return restClient.get().uri("/mai/groups?course={course}&faculty={faculty}", course, faculty)
                .retrieve().body(GROUPS_LIST_TR);
    }
}
