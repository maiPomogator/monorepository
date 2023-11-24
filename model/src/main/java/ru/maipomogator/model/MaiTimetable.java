package ru.maipomogator.model;

import java.util.Collection;

import lombok.Value;

@Value
public class MaiTimetable implements Timetable {
    private final Collection<Lesson> lessons;
    private final Collection<Professor> professors;
    private final Collection<Group> groups;
}
