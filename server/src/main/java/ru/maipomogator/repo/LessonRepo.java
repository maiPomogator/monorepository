package ru.maipomogator.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.maipomogator.model.Lesson;

@Repository
public interface LessonRepo extends JpaRepository<Lesson, Long> {

    @Query("select l from Lesson l")
    List<Lesson> findAllLazy();

    @EntityGraph(attributePaths = { "types", "rooms", "professors", "groups" })
    List<Lesson> findAll();

    /**
     * Возвращает список всех занятий между указанными датами для группы с указанным ID
     * 
     * @implNote выполняет EAGER загрузку всех полей Lesson
     * @param groupId   - ID группы
     * @param startDate - начальная дата
     * @param endDate   - конечная дата
     * @return - список занятий
     */
    @EntityGraph(attributePaths = { "types", "rooms", "professors", "groups" })
    List<Lesson> findByGroupsIdAndDateBetween(long groupId, LocalDate startDate, LocalDate endDate);

    /**
     * Возвращает список всех занятий между указанными датами для преподавателя с указанным ID
     * 
     * @implNote выполняет EAGER загрузку всех полей Lesson
     * @param professorId - ID преподавателя
     * @param startDate   - начальная дата
     * @param endDate     - конечная дата
     * @return - список занятий
     */
    @EntityGraph(attributePaths = { "types", "rooms", "professors", "groups" })
    List<Lesson> findByProfessorsIdAndDateBetween(Long professorId, LocalDate startDate, LocalDate endDate);
}
