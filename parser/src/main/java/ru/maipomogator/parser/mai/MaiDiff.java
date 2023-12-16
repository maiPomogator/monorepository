package ru.maipomogator.parser.mai;

import java.util.Collection;

import lombok.Getter;
import ru.maipomogator.model.Lesson;

@Getter
public class MaiDiff {

    private Collection<Lesson> lessonsToMarkRemoved;
    private Collection<Lesson> lessonsToSave;

    public MaiDiff(Collection<Lesson> lessonsToMarkRemoved, Collection<Lesson> lessonsToSave) {
        this.lessonsToMarkRemoved = lessonsToMarkRemoved;
        this.lessonsToSave = lessonsToSave;
    }

}
