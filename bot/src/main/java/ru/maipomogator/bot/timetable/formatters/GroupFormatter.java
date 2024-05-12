package ru.maipomogator.bot.timetable.formatters;

import org.springframework.stereotype.Component;

import ru.maipomogator.bot.model.Lesson;
import ru.maipomogator.bot.timetable.TimetableTarget;

@Component
class GroupFormatter extends AbsFormatter {

    private GroupFormatter() {
        super(TimetableTarget.GROUP);
    }

    @Override
    public String format(Lesson lesson) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatHeader(lesson)).append(LINE_SEPARATOR);
        sb.append(formatProfessors(lesson.professors())).append(LINE_SEPARATOR);
        sb.append(formatRooms(lesson.rooms())).append(LINE_SEPARATOR);
        return sb.toString();
    }
}
