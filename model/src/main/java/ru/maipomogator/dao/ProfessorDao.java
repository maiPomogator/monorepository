package ru.maipomogator.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import ru.maipomogator.model.Professor;

@Repository
@RequiredArgsConstructor
public class ProfessorDao {

    private final EntityManager em;

    public List<Professor> fetchAllWithAllFields() {
        return em.createQuery("select p from Professor p LEFT JOIN FETCH p.lessons", Professor.class).getResultList();
    }
}
