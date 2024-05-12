package ru.maipomogator.bot.clients;

import java.time.LocalDate;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.bot.model.Lesson;

@RequiredArgsConstructor

@Component
public class TimetableRestClient {
    @NonNull
    static final ParameterizedTypeReference<List<Lesson>> LESSONS_LIST_TR = new ParameterizedTypeReference<>() {};

    private final RestClient restClient;

    public List<Lesson> getLessonsBetweenDates(String target, Long id, LocalDate startDate, LocalDate endDate) {
        return restClient.get()
                .uri("/mai/{target}/{id}/lessons?startDate={startDate}&endDate={endDate}", target, id, startDate, endDate)
                .retrieve().body(LESSONS_LIST_TR);
    }
}
