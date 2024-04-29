package ru.maipomogator.domain.group;

import java.util.List;

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

    public List<Group> findAll() {
        return groupRepo.findAll();
    }

    public List<Group> findByCourseAndFaculty(Integer course, Integer faculty) {
        return groupRepo.findByCourseAndFaculty(course, faculty);
    }

    public List<Group> findByCourseAndFacultyAndType(Integer course, Integer faculty, GroupType type) {
        return groupRepo.findByCourseAndFacultyAndType(course, faculty, type);
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

    public List<Group> findByExample(Group example) {
        example.setIsActive(null);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Group> groupExample = Example.of(example, matcher);

        return groupRepo.findAll(groupExample, Sort.by("name"));
    }
}
