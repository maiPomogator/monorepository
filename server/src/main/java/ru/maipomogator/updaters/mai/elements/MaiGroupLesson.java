package ru.maipomogator.updaters.mai.elements;

import lombok.AllArgsConstructor;
import ru.maipomogator.domain.lesson.Lesson;

/**
 * Костыль, чтобы сделать кастомную десериализацию ответов от API МАИ.
 */
@AllArgsConstructor
public class MaiGroupLesson extends Lesson {
    public Lesson getAsLesson() {
        Lesson lesson = new Lesson();
        lesson.setDate(getDate());
        lesson.setTimeStart(getTimeStart());
        lesson.setTimeEnd(getTimeEnd());
        lesson.setName(getName());
        lesson.setProfessors(getProfessors());
        lesson.setGroups(getGroups());
        lesson.setRooms(getRooms());
        lesson.setTypes(getTypes());

        return lesson;
    }
}
