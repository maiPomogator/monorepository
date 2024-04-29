package ru.maipomogator.domain.group;
@Deprecated
public record GroupLegacyDTO(Long id, String name, Integer course, Integer faculty, GroupType type, Boolean isActive) {}