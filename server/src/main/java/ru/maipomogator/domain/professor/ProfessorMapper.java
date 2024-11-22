package ru.maipomogator.domain.professor;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfessorMapper {
    ProfessorDTO toDTO(Professor professor);

    List<ProfessorDTO> toDTOs(Iterable<Professor> professor);
}