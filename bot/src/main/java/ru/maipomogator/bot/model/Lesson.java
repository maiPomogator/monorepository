package ru.maipomogator.bot.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record Lesson(Long id, String name, List<LessonType> types, LocalDate date, LocalTime timeStart,
        LocalTime timeEnd, List<String> rooms, LessonStatus status, List<Group> groups, List<Professor> professors) {}