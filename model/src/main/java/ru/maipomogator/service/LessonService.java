package ru.maipomogator.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.maipomogator.dao.LessonDao;
import ru.maipomogator.model.Lesson;
import ru.maipomogator.repo.LessonRepo;

@Service
@Transactional(readOnly = true)
public class LessonService {
    private final LessonRepo lessonRepo;
    private final LessonDao lessonDao;

    @Autowired
    public LessonService(LessonRepo lessonRepo, LessonDao lessonDao) {
        this.lessonRepo = lessonRepo;
        this.lessonDao = lessonDao;
    }

    public Optional<Lesson> findById(Long id) {
        return lessonRepo.findById(id);
    }

    public List<Lesson> findAll() {
        return lessonRepo.findAll();
    }

    public List<Lesson> bulkFindAll() {
        return lessonDao.findAll();
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
