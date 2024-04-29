package ru.maipomogator.domain.professor;

import java.util.UUID;

@Deprecated
public record ProfessorLegacyDTO(Long id, String lastName, String firstName, String middleName, UUID siteId) {}
