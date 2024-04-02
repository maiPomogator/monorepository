package ru.maipomogator.parser.adapters;

import java.util.SortedSet;
import java.util.TreeSet;

import lombok.Getter;
import lombok.Setter;
import ru.maipomogator.model.Lesson;

@Setter
@Getter
public class ParsedGroup {

    private SortedSet<Lesson> lessons = new TreeSet<>();
    private String groupName;

    public void addLesson(Lesson newLesson) {
        lessons.add(newLesson);
    }

    public int numberOfLessons() {
        return lessons.size();
    }
}
