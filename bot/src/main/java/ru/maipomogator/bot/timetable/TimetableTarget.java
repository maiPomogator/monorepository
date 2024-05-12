package ru.maipomogator.bot.timetable;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TimetableTarget {
    GROUP("grp"),
    PROFESSOR("prf");

    private final String callbackData;

    public static TimetableTarget fromCallbackData(String callbackData) {
        for (TimetableTarget target : values()) {
            if (target.callbackData.equals(callbackData)) {
                return target;
            }
        }
        throw new IllegalArgumentException("No TimetableTarget for callbackData %s".formatted(callbackData));
    }
}
