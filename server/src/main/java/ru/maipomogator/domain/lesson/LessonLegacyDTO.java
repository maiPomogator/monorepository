package ru.maipomogator.domain.lesson;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import ru.maipomogator.domain.group.GroupLegacyDTO;
import ru.maipomogator.domain.professor.ProfessorLegacyDTO;

@Deprecated
public record LessonLegacyDTO(Long id, String name, Set<LessonType> types, LocalDate date, LocalTime timeStart,
        LocalTime timeEnd, Set<String> rooms, Set<GroupLegacyDTO> groups, Set<ProfessorLegacyDTO> professors,
        Boolean isActive) {}