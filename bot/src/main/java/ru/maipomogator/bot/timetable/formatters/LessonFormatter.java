package ru.maipomogator.bot.timetable.formatters;

import ru.maipomogator.bot.model.Lesson;
import ru.maipomogator.bot.timetable.TimetableTarget;

public interface LessonFormatter {
    String format(Lesson lesson);
    TimetableTarget getTarget();
}
