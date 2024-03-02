package ru.maipomogator.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.model.Professor;
import ru.maipomogator.repo.ProfessorRepo;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfessorService {
    private final ProfessorRepo professorRepo;

    public Optional<Professor> findById(Long id) {
        return professorRepo.findById(id);
    }

    public Optional<Professor> findBySiteId(UUID siteId) {
        return professorRepo.findBySiteId(siteId);
    }

    public List<Professor> findAll() {
        return professorRepo.findAll();
    }

    @Transactional
    public Professor save(Professor professor) {
        return professorRepo.save(professor);
    }

    @Transactional
    public void saveAll(Iterable<Professor> professors) {
        professorRepo.saveAll(professors);
    }

    @Transactional
    public void delete(Long id) {
        professorRepo.deleteById(id);
    }
}
