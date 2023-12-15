package ru.maipomogator.parser.interfaces;

import java.util.Collection;

import ru.maipomogator.model.Group;
import ru.maipomogator.model.Lesson;
import ru.maipomogator.model.Professor;

public interface Timetable {
    Collection<Lesson> getLessons();

    int getNumberOfLessons();

    Collection<Professor> getProfessors();

    Collection<Group> getGroups();
}
