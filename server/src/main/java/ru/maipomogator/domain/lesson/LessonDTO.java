package ru.maipomogator.domain.lesson;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Set;

import ru.maipomogator.domain.group.GroupDTO;
import ru.maipomogator.domain.professor.ProfessorDTO;

public record LessonDTO(Long id, String name, Collection<LessonType> types, LocalDate date, LocalTime timeStart,
        LocalTime timeEnd, Set<String> rooms, Set<GroupDTO> groups, Set<ProfessorDTO> professors,
        Boolean isActive) {}