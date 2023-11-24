package ru.maipomogator.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.maipomogator.model.Lesson;
import ru.maipomogator.model.Note;

@Repository
public interface NoteRepo extends JpaRepository<Note, Long> {
    List<Note> findByLesson(Lesson lesson);
}
