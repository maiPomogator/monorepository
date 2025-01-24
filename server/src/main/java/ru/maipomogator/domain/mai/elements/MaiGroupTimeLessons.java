package ru.maipomogator.domain.mai.elements;

import java.util.ArrayList;
import java.util.Collection;

import ru.maipomogator.domain.lesson.Lesson;

public class MaiGroupTimeLessons extends ArrayList<MaiGroupLesson> {
    public MaiGroupTimeLessons(Collection<? extends MaiGroupLesson> c) {
        super(c);
    }

    public Collection<Lesson> getAsLessons() {
        return this.stream().map(MaiGroupLesson::getAsLesson).toList();
    }
}
