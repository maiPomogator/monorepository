package ru.maipomogator.domain.lesson;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Deprecated
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface LessonLegacyMapper {
    List<LessonLegacyDTO> toLegacyDTOs(Iterable<Lesson> lessons);
}
