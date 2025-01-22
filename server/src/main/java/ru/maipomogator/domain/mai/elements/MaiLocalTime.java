package ru.maipomogator.domain.mai.elements;

import java.time.LocalTime;
/**
 * Костыль для времени в ответах МАИ API в формате "H:mm:ss".
 */
public record MaiLocalTime(LocalTime time) {}
