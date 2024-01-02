package ru.maipomogator.parser.mai;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.maipomogator.model.Lesson;

@Getter
@AllArgsConstructor
public class MaiDiff {
    private Collection<Lesson> lessonsToDisable;
    private Collection<Lesson> lessonsToEnable;
    private Collection<Lesson> lessonsToSave;
}
