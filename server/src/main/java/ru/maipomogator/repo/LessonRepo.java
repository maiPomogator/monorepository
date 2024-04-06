package ru.maipomogator.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.maipomogator.model.Lesson;

@Repository
public interface LessonRepo extends JpaRepository<Lesson, Long> {

    @Query("select l from Lesson l")
    List<Lesson> findAllLazy();

    @EntityGraph(attributePaths = { "types", "rooms", "professors", "groups" })
    List<Lesson> findAll();

    @Query("SELECT DISTINCT l.id FROM Lesson l JOIN l.groups g WHERE g.id = :groupId AND l.date BETWEEN :startDate AND :endDate")
    List<Long> findLessonIdsByGroupIdAndDateBetween(
            @Param("groupId") Long groupId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT DISTINCT l.id FROM Lesson l JOIN l.professors p WHERE p.id = :professorId AND l.date BETWEEN :startDate AND :endDate")
    List<Long> findLessonIdsByProfessorIdAndDateBetween(
            @Param("professorId") Long professorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @EntityGraph(attributePaths = { "types", "rooms", "professors", "groups" })
    List<Lesson> findAllByIdInOrderByDateAscTimeStartAsc(List<Long> lessonIds);
}
