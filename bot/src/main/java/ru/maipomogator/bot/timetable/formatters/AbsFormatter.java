package ru.maipomogator.bot.timetable.formatters;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.maipomogator.bot.model.Group;
import ru.maipomogator.bot.model.Lesson;
import ru.maipomogator.bot.model.LessonType;
import ru.maipomogator.bot.model.Professor;
import ru.maipomogator.bot.timetable.TimetableTarget;

@Getter
@RequiredArgsConstructor
abstract class AbsFormatter implements LessonFormatter {
    protected static final String LINE_SEPARATOR = System.lineSeparator();
    private final TimetableTarget target;

    protected String formatHeader(Lesson lesson) {
        return lesson.name() + " (" + formatTypes(lesson.types()) + ") с " + lesson.timeStart() + " до "
                + lesson.timeEnd();
    }

    protected String formatTypes(List<LessonType> types) {
        return types.stream().map(LessonType::getShortName).collect(Collectors.joining(", "));
    }

    protected String formatProfessors(Collection<Professor> professors) {
        return professors.stream().map(Professor::fio).collect(Collectors.joining(", "));
    }

    protected String formatGroups(Collection<Group> groups) {
        return groups.stream().map(Group::name).collect(Collectors.joining(", "));
    }

    protected String formatRooms(Collection<String> rooms) {
        return rooms.stream().collect(Collectors.joining(", "));
    }
}
