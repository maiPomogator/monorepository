package ru.maipomogator.domain.group;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepo extends JpaRepository<Group, Long> {

    Group findByNameIgnoreCase(String name);

    List<Group> findByCourseAndFaculty(Integer course, Integer faculty);

    List<Group> findByCourseAndFacultyAndType(Integer course, Integer faculty, GroupType type);

    @Query("SELECT DISTINCT g.faculty FROM Group g ORDER BY g.faculty ASC")
    List<String> getAllFaculties();

    @Query("SELECT COUNT(DISTINCT g.course) FROM Group g")
    Integer getNumberOfCourses();

}
