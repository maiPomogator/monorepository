package ru.maipomogator.bot.timetable;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.stereotype.Component;

import ru.maipomogator.bot.clients.GroupRestClient;
import ru.maipomogator.bot.clients.ProfessorRestClient;
import ru.maipomogator.bot.model.Lesson;
import ru.maipomogator.bot.service.TimetableService;
import ru.maipomogator.bot.timetable.formatters.LessonFormatter;
import ru.maipomogator.bot.timetable.formatters.LessonFormatterSelector;

@Component
public class PeriodTimetableBuilder extends AbstractTimetableBuilder {

    public PeriodTimetableBuilder(GroupRestClient groupRestClient, ProfessorRestClient professorRestClient,
            TimetableService timetableService, LessonFormatterSelector selector) {
        super(groupRestClient, professorRestClient, timetableService, selector);
    }

    @Override
    protected String getMessageText(TimetableTarget target, Long entityId, LocalDate startDate,
            LocalDate endDate) {
        StringBuilder sb = new StringBuilder();
        LessonFormatter formatter = selector.select(target);
        Collection<Lesson> lessons = getLessons(target, entityId, startDate, endDate);
        LocalDate date = startDate;
        do {
            Collection<Lesson> dayLessons = getDayLessons(lessons, date);
            sb.append(getFormattedDay(formatter, date, dayLessons));
            date = date.plusDays(1);
        } while (date.isBefore(endDate));
        return sb.toString();
    }

    private Collection<Lesson> getDayLessons(Collection<Lesson> lessons, LocalDate date) {
        return lessons.stream().filter(l -> l.date().equals(date)).toList();
    }
}
