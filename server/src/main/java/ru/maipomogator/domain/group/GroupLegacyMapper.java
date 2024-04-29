package ru.maipomogator.domain.group;

import java.util.List;

import org.mapstruct.Mapper;

@Deprecated
@Mapper(componentModel = "spring")
public interface GroupLegacyMapper {
    GroupLegacyDTO toLegacyDTO(Group group);

    List<GroupLegacyDTO> toLegacyDTOs(Iterable<Group> group);
}
