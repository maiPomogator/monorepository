package ru.maipomogator.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.dao.LessonDao;
import ru.maipomogator.model.Lesson;
import ru.maipomogator.repo.LessonRepo;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepo lessonRepo;
    private final LessonDao lessonDao;

    public Optional<Lesson> findById(Long id) {
        return lessonRepo.findById(id);
    }

    public List<Lesson> findAll() {
        return lessonRepo.findAll();
    }

    public List<Lesson> bulkFindAllWithRoomsAndTypes() {
        return lessonDao.fetchAllWithRoomsAndTypes();
    }

    public List<Lesson> bulkFindAllWithAllFields() {
        return lessonDao.fetchAllWithAllFields();
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
