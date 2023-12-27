package ru.maipomogator.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.model.Lesson;
import ru.maipomogator.model.Note;
import ru.maipomogator.repo.NoteRepo;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepo noteRepo;

    public Optional<Note> findById(Long id) {
        return noteRepo.findById(id);
    }

    public List<Note> findByLesson(Lesson lesson) {
        return noteRepo.findByLesson(lesson);
    }

    public List<Note> findAll() {
        return noteRepo.findAll();
    }

    @Transactional
    public Note save(Note note) {
        return noteRepo.save(note);
    }

    @Transactional
    public void saveAll(Iterable<Note> notes) {
        noteRepo.saveAll(notes);
    }

    @Transactional
    public void delete(Long id) {
        noteRepo.deleteById(id);
    }
}
