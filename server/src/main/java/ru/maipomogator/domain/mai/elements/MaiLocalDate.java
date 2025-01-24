package ru.maipomogator.domain.mai.elements;

import java.time.LocalDate;
/**
 * Костыль для даты в ответах API МАИ в формате "dd.MM.yyyy".
 */
public record MaiLocalDate(LocalDate date) {
    @Override
    public String toString() {
        return date.toString();
    }
}
