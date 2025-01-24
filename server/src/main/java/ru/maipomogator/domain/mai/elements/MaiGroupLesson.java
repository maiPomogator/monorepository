package ru.maipomogator.domain.mai.elements;

import lombok.AllArgsConstructor;
import ru.maipomogator.domain.lesson.Lesson;

/**
 * Костыль, чтобы сделать кастомную десериализацию ответов от API МАИ.
 */
@AllArgsConstructor
public class MaiGroupLesson extends Lesson {
    public Lesson getAsLesson() {
        return this;
    }
}
