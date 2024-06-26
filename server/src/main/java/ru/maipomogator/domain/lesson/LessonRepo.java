package ru.maipomogator.domain.lesson;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepo extends JpaRepository<Lesson, Long> {

    @Query("SELECT DISTINCT l.id FROM Lesson l JOIN l.groups g WHERE g.id IN :groupIds")
    List<Long> findLessonIdsByGroupIds(@Param("groupIds") List<Long> groupIds);

    @Query("SELECT DISTINCT l.id FROM Lesson l JOIN l.groups g WHERE g.id = :groupId")
    List<Long> findLessonIdsByGroupId(@Param("groupId") Long groupId);

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

    default List<Lesson> findEagerByIdInOrderByDateAscTimeStartAsc(List<Long> lessonIds) {
        return findEagerByIdIn(lessonIds, Sort.by("date", "timeStart"));
    }

    @EntityGraph(attributePaths = { "types", "rooms", "professors", "groups" })
    List<Lesson> findEagerByIdIn(List<Long> lessonIds, Sort sort);
}
