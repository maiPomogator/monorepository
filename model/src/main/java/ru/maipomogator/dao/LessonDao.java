package ru.maipomogator.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import ru.maipomogator.model.Lesson;

@Repository
public class LessonDao {

    private final EntityManager em;

    @Autowired
    public LessonDao(EntityManager em) {
        this.em = em;
    }

    public List<Lesson> findAll() {
        System.out.println("Custom query");
        return em.createQuery(
                "select l from Lesson l LEFT JOIN FETCH l.groups LEFT JOIN FETCH l.professors LEFT JOIN FETCH l.rooms LEFT JOIN FETCH l.types",
                Lesson.class).getResultList();
    }

}
