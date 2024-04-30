package ru.maipomogator.domain.group;

import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    GroupDTO toDTO(Group group);

    List<GroupDTO> toDTOs(Iterable<Group> group);
}