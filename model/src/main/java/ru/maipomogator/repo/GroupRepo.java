package ru.maipomogator.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.maipomogator.model.Group;
import ru.maipomogator.model.GroupType;

@Repository
public interface GroupRepo extends JpaRepository<Group, Long> {
    List<Group> findByCourseAndFaculty(Integer course, Integer faculty);

    List<Group> findByCourseAndFacultyAndType(Integer course, Integer faculty, GroupType type);
}