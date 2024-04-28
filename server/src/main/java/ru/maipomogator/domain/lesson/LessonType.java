package ru.maipomogator.domain.lesson;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString(of = {})
public enum LessonType {
    LECTURE("Лекция", "ЛК"),
    PRACTICE("Практическое занятие", "ПЗ"),
    LABORATORY("Лабораторная работа", "ЛР"),
    CONSULTATION("Консультация", "Конс."),
    CREDIT("Зачёт", "Зач."),
    EXAM("Экзамен", "Экз.");

    private final String name;
    private final String shortName;
}