package ru.maipomogator.domain.group;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepo groupRepo;
    @Deprecated
    private final GroupLegacyMapper legacyMapper;

    public List<Group> findAll() {
        return groupRepo.findAll();
    }

    @Cacheable(value = "allFaculties")
    public List<String> findAllFaculties() {
        return groupRepo.getAllFaculties();
    }

    @Cacheable(value = "numberOfCourses")
    public Integer getNumberOfCourses() {
        return groupRepo.getNumberOfCourses();
    }

    @Transactional
    public Group save(Group group) {
        return groupRepo.save(group);
    }

    @Transactional
    public List<Group> saveAll(Iterable<Group> groups) {
        return groupRepo.saveAll(groups);
    }

    @Transactional
    public void delete(Long id) {
        groupRepo.deleteById(id);
    }

    @Deprecated
    public Optional<GroupLegacyDTO> legacyGetOneById(Long id) {
        return groupRepo.findById(id).map(legacyMapper::toLegacyDTO);
    }

    @Deprecated
    public List<GroupLegacyDTO> legacyFindAll() {
        return legacyMapper.toLegacyDTOs(groupRepo.findAll());
    }

    @Deprecated
    public List<GroupLegacyDTO> legacyFindByCourseAndFaculty(Integer course, Integer faculty) {
        return legacyMapper.toLegacyDTOs(groupRepo.findByCourseAndFaculty(course, faculty));
    }

    @Deprecated
    public List<GroupLegacyDTO> legacyFindByCourseAndFacultyAndType(Integer course, Integer faculty, GroupType type) {
        return legacyMapper.toLegacyDTOs(groupRepo.findByCourseAndFacultyAndType(course, faculty, type));
    }

    @Deprecated
    public List<GroupLegacyDTO> legacyFindByExample(String name) {
        return legacyMapper.toLegacyDTOs(findByExample(name));
    }

    private List<Group> findByExample(String name) {
        Group example = new Group();
        example.setName(name);
        example.setIsActive(null);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Group> groupExample = Example.of(example, matcher);

        return groupRepo.findAll(groupExample, Sort.by("name"));
    }
}
