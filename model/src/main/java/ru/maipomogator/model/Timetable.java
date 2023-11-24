package ru.maipomogator.model;

import java.util.Collection;

public interface Timetable {
    public Collection<Lesson> getLessons();

    Collection<Professor> getProfessors();

    Collection<Group> getGroups();
}
