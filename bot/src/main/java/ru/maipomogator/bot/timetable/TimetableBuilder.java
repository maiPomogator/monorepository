package ru.maipomogator.bot.timetable;

import java.time.LocalDate;

public interface TimetableBuilder {
    String getMessageText(String targetInfo, LocalDate startDate, LocalDate endDate);
}
