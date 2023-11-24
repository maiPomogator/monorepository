package ru.maipomogator.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.maipomogator.model.Professor;


@Repository
public interface ProfessorRepo extends JpaRepository<Professor, Long> {
    Optional<Professor> findBySiteId(UUID siteId);
}