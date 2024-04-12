package ru.maipomogator.bot.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

public record Lesson(Long id, String name, List<LessonType> types, LocalDate date, LocalTime timeStart,
        LocalTime timeEnd, List<String> rooms, Boolean isActive, List<Group> groups, List<Professor> professors)
        implements Comparable<Lesson> {

    @Override
    public int compareTo(Lesson other) {
        return Comparator.comparing(Lesson::date).thenComparing(Lesson::timeStart).compare(this, other);
    }
}