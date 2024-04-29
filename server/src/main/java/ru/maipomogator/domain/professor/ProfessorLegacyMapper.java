package ru.maipomogator.domain.professor;

import java.util.List;

import org.mapstruct.Mapper;

@Deprecated
@Mapper(componentModel = "spring")
public interface ProfessorLegacyMapper {
    ProfessorLegacyDTO toLegacyDTO(Professor group);

    List<ProfessorLegacyDTO> toDTOs(Iterable<Professor> group);

}
