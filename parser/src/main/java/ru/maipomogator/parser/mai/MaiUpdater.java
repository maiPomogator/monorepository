package ru.maipomogator.parser.mai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.maipomogator.model.Group;
import ru.maipomogator.model.Lesson;
import ru.maipomogator.model.LessonStatus;
import ru.maipomogator.model.Professor;
import ru.maipomogator.service.GroupService;
import ru.maipomogator.service.LessonService;
import ru.maipomogator.service.ProfessorService;

@Log4j2
@Component
@RequiredArgsConstructor
public class MaiUpdater {
    private final LessonService lessonService;
    private final ProfessorService professorService;
    private final GroupService groupService;
    private final MaiParser parser;

    @Scheduled(fixedRate = 12, timeUnit = TimeUnit.HOURS)
    @Transactional
    public void update() {
        MaiTimetable oldTimetable = getOldTimetable();
        log.info("Got old timetable ({} lessons, {} groups, {} professors)",
                oldTimetable.getLessons().size(), oldTimetable.getGroups().size(), oldTimetable.getProfessors().size());
        MaiTimetable newTimetable = parser.getTimetable();
        log.info("Got new timetable ({} lessons, {} groups, {} professors)",
                newTimetable.getLessons().size(), newTimetable.getGroups().size(), newTimetable.getProfessors().size());
        MaiDiff diff = merge(oldTimetable, newTimetable);
        log.info("Got diff ({} to disable, {} to enable, {} to save)",
                diff.getLessonsToDisable().size(), diff.getLessonsToEnable().size(), diff.getLessonsToSave().size());
        saveDiff(diff);
        log.info("Finished update");
    }

    private void saveDiff(MaiDiff diff) {
        lessonService.saveAll(diff.getLessonsToSave());
        lessonService.saveAll(diff.getLessonsToDisable());
        lessonService.saveAll(diff.getLessonsToEnable());
    }

    //TODO проверить наличие проблемы N+1 в группах и преподавателях
    private MaiTimetable getOldTimetable() {
        Map<Long, Lesson> lessons = lessonService.findAllEager().stream()
                .collect(Collectors.toMap(Lesson::getHash, l -> l));
        Map<String, Group> groups = groupService.findAll().stream()
                .collect(Collectors.toMap(Group::getName, gr -> gr));
        Map<UUID, Professor> professors = professorService.findAll().stream()
                .collect(Collectors.toMap(Professor::getSiteId, pr -> pr));
        return new MaiTimetable(lessons, professors, groups);
    }

    private MaiDiff merge(MaiTimetable oldTimetable, MaiTimetable newTimetable) {
        Map<Long, Lesson> oldLessons = oldTimetable.getLessons();
        Map<String, Group> oldGroups = oldTimetable.getGroups();
        Map<UUID, Professor> oldProfessors = oldTimetable.getProfessors();

        Map<Long, Lesson> existingLessons = new HashMap<>();
        Map<Long, Lesson> lessonsToDisable = new HashMap<>();
        Map<Long, Lesson> lessonsToEnable = new HashMap<>();
        Map<Long, Lesson> lessonsToSave = new HashMap<>();

        Map<Long, Lesson> newLessons = newTimetable.getLessons();

        for (Entry<Long, Lesson> entry : newLessons.entrySet()) {
            if (oldLessons.containsKey(entry.getKey())) {
                Lesson oldLesson = oldLessons.get(entry.getKey());
                if (oldLesson.getStatus().equals(LessonStatus.SAVED)) {
                    existingLessons.put(entry.getKey(), oldLesson);
                } else {
                    oldLesson.setStatus(LessonStatus.SAVED);
                    lessonsToEnable.put(entry.getKey(), oldLesson);
                }
                oldLessons.remove(entry.getKey());
            } else {
                Lesson orig = entry.getValue();
                Lesson copy = Lesson.copyOf(orig);

                Set<Group> newLessonGroups = new HashSet<>(copy.getGroups().size());
                for (Group group : orig.getGroups()) {
                    if (oldGroups.containsKey(group.getName())) {
                        newLessonGroups.add(oldGroups.get(group.getName()));
                    } else {
                        newLessonGroups.add(group);
                    }
                }
                copy.setGroups(newLessonGroups);

                Set<Professor> newLessonProfessors = new HashSet<>(copy.getProfessors().size());
                for (Professor professor : orig.getProfessors()) {
                    if (oldProfessors.containsKey(professor.getSiteId())) {
                        newLessonProfessors.add(oldProfessors.get(professor.getSiteId()));
                    } else {
                        newLessonProfessors.add(professor);
                    }
                }
                copy.setProfessors(newLessonProfessors);

                lessonsToSave.put(entry.getKey(), copy);
            }
        }

        for (Entry<Long, Lesson> entry : oldLessons.entrySet()) {
            entry.getValue().setStatus(LessonStatus.CANCELLED);
            lessonsToDisable.put(entry.getKey(), entry.getValue());
        }

        return new MaiDiff(lessonsToDisable.values(), lessonsToEnable.values(), lessonsToSave.values());
    }
}