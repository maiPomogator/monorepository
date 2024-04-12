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

    public List<Lesson> findAllLessonsForGroup(Group group) {
        List<Long> lessonIds = lessonRepo.findLessonIdsByGroupId(group.getId());
        if (lessonIds.isEmpty()) {
            return List.of();
        }
        return lessonRepo.findLazyByIdInOrderByDateAscTimeStartAsc(lessonIds);
    }

    public List<Lesson> findEagerForGroupBetweenDates(Group group, LocalDate startDate, LocalDate endDate) {
        List<Long> lessonIds = lessonRepo.findLessonIdsByGroupIdAndDateBetween(group.getId(), startDate, endDate);
        if (lessonIds.isEmpty()) {
            return List.of();
        }
        return lessonRepo.findEagerByIdInOrderByDateAscTimeStartAsc(lessonIds);
    }

    public List<Lesson> findEagerForProfessorBetweenDates(Professor professor, LocalDate startDate, LocalDate endDate) {
        List<Long> lessonIds = lessonRepo.findLessonIdsByProfessorIdAndDateBetween(professor.getId(), startDate,
                endDate);
        if (lessonIds.isEmpty()) {
            return List.of();
        }
        return lessonRepo.findEagerByIdInOrderByDateAscTimeStartAsc(lessonIds);
    }
}
