package ru.maipomogator.parser.mai;

import java.util.Collection;

import lombok.Value;
import ru.maipomogator.model.Group;
import ru.maipomogator.model.Lesson;
import ru.maipomogator.model.Professor;
import ru.maipomogator.parser.interfaces.Timetable;

@Value
public class MaiTimetable implements Timetable {
    private final Collection<Lesson> lessons;
    private final Collection<Professor> professors;
    private final Collection<Group> groups;

    @Override
    public int getNumberOfLessons() {
        return lessons.size();
    }
}
