package ru.maipomogator.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import ru.maipomogator.model.Group;

@Repository
@RequiredArgsConstructor
public class GroupDao {

    private final EntityManager em;

    public List<Group> fetchAllWithAllFields() {
        return em.createQuery("select g from Group g LEFT JOIN FETCH g.lessons", Group.class).getResultList();
    }
}
