package ru.maipomogator.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import ru.maipomogator.model.Lesson;

@Repository
@RequiredArgsConstructor
public class LessonDao {

    private final EntityManager em;

    public List<Lesson> fetchAllWithRoomsAndTypes() {
        return em.createQuery(
                "select l from Lesson l LEFT JOIN FETCH l.rooms LEFT JOIN FETCH l.types",
                Lesson.class).getResultList();
    }

    public List<Lesson> fetchAllWithAllFields() {
        return em.createQuery(
                "select l from Lesson l LEFT JOIN FETCH l.groups LEFT JOIN FETCH l.professors LEFT JOIN FETCH l.rooms LEFT JOIN FETCH l.types",
                Lesson.class).getResultList();
    }
}
