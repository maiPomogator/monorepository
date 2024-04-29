package ru.maipomogator.domain.lesson;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Deprecated
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LessonLegacyMapper {
    @Mapping(target = "isActive", source = "active")
    List<LessonLegacyDTO> toLegacyDTOs(Iterable<Lesson> lessons);
}
