package ru.maipomogator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.maipomogator.model.Lesson;

@Repository
public interface LessonRepo extends JpaRepository<Lesson, Long> {}