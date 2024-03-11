package ru.maipomogator.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.model.Group;
import ru.maipomogator.model.GroupType;
import ru.maipomogator.repo.GroupRepo;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepo groupRepo;

    public Optional<Group> findById(Long id) {
        return groupRepo.findById(id);
    }

    public List<Group> findAll() {
        return groupRepo.findAll();
    }

    public List<Group> findByCourseAndFaculty(Integer course, Integer faculty) {
        return groupRepo.findByCourseAndFaculty(course, faculty);
    }

    public List<Group> findByCourseAndFacultyAndType(Integer course, Integer faculty, GroupType type) {
        return groupRepo.findByCourseAndFacultyAndType(course, faculty, type);
    }

    @Transactional
    public Group save(Group group) {
        return groupRepo.save(group);
    }

    @Transactional
    public void saveAll(Iterable<Group> groups) {
        groupRepo.saveAll(groups);
    }

    @Transactional
    public void delete(Long id) {
        groupRepo.deleteById(id);
    }

    public Group findByName(String name) {
        return groupRepo.findByName(name);
    }

}
