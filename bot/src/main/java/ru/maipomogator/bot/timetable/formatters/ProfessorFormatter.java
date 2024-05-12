package ru.maipomogator.bot.timetable.formatters;

import org.springframework.stereotype.Component;

import ru.maipomogator.bot.model.Lesson;
import ru.maipomogator.bot.timetable.TimetableTarget;

@Component
class ProfessorFormatter extends AbsFormatter {

    private ProfessorFormatter() {
        super(TimetableTarget.PROFESSOR);
    }

    @Override
    public String format(Lesson lesson) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatHeader(lesson)).append(LINE_SEPARATOR);
        sb.append(formatGroups(lesson.groups())).append(LINE_SEPARATOR);
        sb.append(formatRooms(lesson.rooms())).append(LINE_SEPARATOR);
        return sb.toString();
    }
}
