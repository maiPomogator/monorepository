package ru.maipomogator.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString(of = {})
public enum GroupType {
    BACHELOR("Бакалавриат"),
    MAGISTRACY("Магистратура"),
    POSTGRADUATE("Аспирантура"),
    SPECIALIST("Специалитет"),
    BASIC_HIGHER_EDUCATION("Базовое высшее образование"),
    SPECIALIZED_HIGHER_EDUCATION("Специализированное высшее образование");

    private final String name;
}
