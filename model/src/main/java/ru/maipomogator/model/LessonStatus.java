package ru.maipomogator.model;

public enum LessonStatus {
    /**
     * Занятие создано, но ещё не загружено в БД
     */
    CREATED,
    /**
     * Занятие загружено в БД
     */
    SAVED,
    /**
     * Занятие было удалено из исходных данных расписания
     */
    CANCELLED
}
