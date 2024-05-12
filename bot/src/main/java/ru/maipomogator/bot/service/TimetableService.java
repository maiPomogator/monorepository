package ru.maipomogator.bot.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.bot.clients.TimetableRestClient;
import ru.maipomogator.bot.model.Lesson;
import ru.maipomogator.bot.model.LessonType;

@RequiredArgsConstructor

@Service
public class TimetableService {
    private static final LocalDate SESSION_START = LocalDate.of(2024, 5, 1);
    private static final LocalDate SESSION_END = LocalDate.of(2024, 7, 1);
    private final TimetableRestClient client;

    public List<Lesson> getProfessorLessons(Long professorId, LocalDate startDate, LocalDate endDate) {
        return client.getLessonsBetweenDates("professors", professorId, startDate, endDate)
                .stream().filter(Lesson::isActive).sorted().toList();
    }

    public List<Lesson> getGroupLessons(Long groupId, LocalDate startDate, LocalDate endDate) {
        return client.getLessonsBetweenDates("groups", groupId, startDate, endDate)
                .stream().filter(Lesson::isActive).sorted().toList();
    }

    public List<Lesson> getExamsForProfessor(Long professorId) {
        return client.getLessonsBetweenDates("professors", professorId, SESSION_START, SESSION_END)
                .stream().filter(l -> l.types().contains(LessonType.EXAM)).filter(Lesson::isActive).sorted().toList();
    }

    public List<Lesson> getExamsForGroup(Long groupId) {
        return client.getLessonsBetweenDates("groups", groupId, SESSION_START, SESSION_END)
                .stream().filter(l -> l.types().contains(LessonType.EXAM)).filter(Lesson::isActive).sorted().toList();
    }
}
