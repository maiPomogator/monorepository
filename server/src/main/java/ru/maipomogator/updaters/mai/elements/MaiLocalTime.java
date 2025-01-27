package ru.maipomogator.updaters.mai.elements;

import java.time.LocalTime;
/**
 * Костыль для времени в ответах API МАИ в формате "H:mm:ss".
 */
public record MaiLocalTime(LocalTime time) {
    @Override
    public String toString() {
        return time.toString();
    }
}
