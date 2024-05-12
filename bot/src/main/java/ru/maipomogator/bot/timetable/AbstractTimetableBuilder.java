package ru.maipomogator.bot.timetable;

import static java.lang.System.lineSeparator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.bot.clients.GroupRestClient;
import ru.maipomogator.bot.clients.ProfessorRestClient;
import ru.maipomogator.bot.model.Group;
import ru.maipomogator.bot.model.Lesson;
import ru.maipomogator.bot.model.Professor;
import ru.maipomogator.bot.service.TimetableService;
import ru.maipomogator.bot.timetable.formatters.LessonFormatterSelector;
import ru.maipomogator.bot.timetable.formatters.LessonFormatter;

@RequiredArgsConstructor
public abstract class AbstractTimetableBuilder implements TimetableBuilder {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, d MMMM",
            Locale.of("ru"));

    protected final GroupRestClient groupRestClient;
    protected final ProfessorRestClient professorRestClient;

    protected final TimetableService timetableService;
    protected final LessonFormatterSelector selector;

    @Override
    public String getMessageText(String targetInfo, LocalDate startDate, LocalDate endDate) {
        String[] parts = targetInfo.split("=");
        TimetableTarget target = TimetableTarget.fromCallbackData(parts[0]);
        Long entityId = Long.parseLong(parts[1]);
        StringBuilder sb = new StringBuilder();

        sb.append(getMessageHeader(target, entityId)).append(lineSeparator());
        sb.append(getMessageText(target, entityId, startDate, endDate));

        return sb.toString();
    }

    protected abstract String getMessageText(
            TimetableTarget target, Long entityId, LocalDate startDate, LocalDate endDate);

    protected String getFormattedDay(LessonFormatter formatter, LocalDate date, Collection<Lesson> dayLessons) {
        StringBuilder sb = new StringBuilder();
        sb.append("*" + formatDate(date) + "*").append(lineSeparator()).append(lineSeparator());

        if (dayLessons.isEmpty()) {
            sb.append("_Пар нет_").append(lineSeparator()).append(lineSeparator());
        } else {
            for (Lesson lesson : dayLessons) {
                sb.append(formatter.format(lesson)).append(lineSeparator());
            }
        }

        return sb.toString();
    }

    private String getMessageHeader(TimetableTarget target, Long entityId) {
        return switch (target) {
            case GROUP -> {
                Group group = groupRestClient.findById(entityId);
                yield "*__Группа " + group.name() + "__*" + lineSeparator();
            }
            case PROFESSOR -> {
                Professor professor = professorRestClient.findById(entityId);
                yield "*__" + professor.fio() + "__*" + lineSeparator();
            }
        };
    }

    protected Collection<Lesson> getLessons(
            TimetableTarget target, Long entityId, LocalDate startDate, LocalDate endDate) {
        return switch (target) {
            case GROUP -> timetableService.getGroupLessons(entityId, startDate, endDate);
            case PROFESSOR -> timetableService.getProfessorLessons(entityId, startDate, endDate);
        };
    }

    private String formatDate(LocalDate date) {
        String str = date.format(DATE_FORMATTER);
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
