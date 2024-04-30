package ru.maipomogator.domain.lesson;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LessonMapper {
    @Mapping(target = "isActive", source = "active")
    LessonDTO toDTO(Lesson lesson);

    List<LessonDTO> toDTOs(Iterable<Lesson> lessons);
}