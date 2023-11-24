package ru.maipomogator.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.maipomogator.model.Lesson;
import ru.maipomogator.repo.LessonRepo;

@Service
@Transactional(readOnly = true)
public class LessonService {
    private final LessonRepo lessonRepo;

    @Autowired
    public LessonService(LessonRepo lessonRepo) {
        this.lessonRepo = lessonRepo;
    }

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
    public void saveAll(Iterable<Lesson> lessons) {
        lessonRepo.saveAll(lessons);
    }

    @Transactional
    public void delete(Long id) {
        lessonRepo.deleteById(id);
    }
}
