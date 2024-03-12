package ru.maipomogator.bot.clients;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import ru.maipomogator.bot.model.Group;
import ru.maipomogator.bot.model.Lesson;

@Component
public class GroupRestClient {

    private static final ParameterizedTypeReference<List<Group>> GROUPS_LIST_TR = new ParameterizedTypeReference<>() {    };
    private static final ParameterizedTypeReference<List<Lesson>> LESSONS_LIST_TR = new ParameterizedTypeReference<>() {    };

    private final RestClient restClient;

    public GroupRestClient(@Value("${baseurl:https://rufus20145.ru}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public Group findByName(String name) {
        return restClient.get().uri("/mai/groups?name={name}", name.toUpperCase()).retrieve().body(Group.class);
    }

    public List<Group> findByCourseAndFaculty(int course, int faculty) {
        return restClient.get().uri("mai/groups?course={course}&faculty={faculty}", course, faculty)
                .retrieve().body(GROUPS_LIST_TR);
    }

    public List<Lesson> getLessons(String id) {
        return restClient.get().uri("/mai/groups/{id}/lessons", id).retrieve().body(LESSONS_LIST_TR);
    }

    public List<Lesson> getLessonsBetweenDates(String id, LocalDate startDate, LocalDate endDate) {
        return restClient.get()
                .uri("/mai/groups/{id}/lessons?startDate={startDate}&endDate={endDate}", id, startDate, endDate)
                .retrieve().body(LESSONS_LIST_TR);
    }

    public Group findById(String id) {
        return restClient.get().uri("/mai/groups/{id}", id).retrieve().body(Group.class);
    }
}
