package ru.maipomogator.parser.mai;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.Value;
import ru.maipomogator.model.Group;
import ru.maipomogator.model.Lesson;
import ru.maipomogator.model.Professor;

@Value
@Component
public class MaiTimetable  {
    private final Map<Long, Lesson> lessons;
    private final Map<UUID, Professor> professors;
    private final Map<String, Group> groups;
}
