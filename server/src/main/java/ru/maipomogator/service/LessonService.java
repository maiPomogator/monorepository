package ru.maipomogator.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.model.Group;
import ru.maipomogator.model.Lesson;
import ru.maipomogator.model.Professor;
import ru.maipomogator.repo.LessonRepo;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepo lessonRepo;

    public Optional<Lesson> findById(Long id) {
        return lessonRepo.findById(id);
    }

    public List<Lesson> findAll() {
        return lessonRepo.findAllLazy();
    }

    public List<Lesson> findAllEager() {
        return lessonRepo.findAll();
    }

    @Transactional
    public Lesson save(Lesson lesson) {
        return lessonRepo.save(lesson);
    }

    @Transactional
    public List<Lesson> saveAll(Iterable<Lesson> lessons) {
        return lessonRepo.saveAll(lessons);
    }

    @Transactional
    public void delete(Long id) {
        lessonRepo.deleteById(id);
    }

    public List<Lesson> findForGroupBetweenDates(Group group, LocalDate startDate, LocalDate endDate) {
        return lessonRepo.findByGroupsIdAndDateBetween(group.getId(), startDate, endDate);
    }

    public List<Lesson> findForProfessorBetweenDates(Professor professor, LocalDate startDate, LocalDate endDate) {
        return lessonRepo.findByProfessorsIdAndDateBetween(professor.getId(), startDate, endDate);
    }
}
