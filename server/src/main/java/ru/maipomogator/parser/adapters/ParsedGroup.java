package ru.maipomogator.parser.adapters;

import java.util.SortedSet;
import java.util.TreeSet;

import lombok.Data;
import ru.maipomogator.model.Lesson;

@Data
public class ParsedGroup {

    private SortedSet<Lesson> lessons = new TreeSet<>();
    private String groupName;

    public void add(Lesson newLesson) {
        lessons.add(newLesson);
    }

    public int size() {
        return lessons.size();
    }
}
