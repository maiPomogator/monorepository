package ru.maipomogator.domain.mai.elements;

import java.util.Collection;
import java.util.Map;

import lombok.AllArgsConstructor;
import ru.maipomogator.domain.lesson.Lesson;

@AllArgsConstructor
public class MaiGroupDay {

    private Map<MaiLocalTime, MaiGroupTimeLessons> pairs;

    public Collection<Lesson> getLessons() {
        return pairs.values().stream()
                .map(MaiGroupTimeLessons::getAsLessons).flatMap(Collection::stream).toList();
    }
}
