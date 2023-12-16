package ru.maipomogator.parser.mai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
public class MaiUpdater {
    private final LessonService lessonService;
    private final ProfessorService professorService;
    private final GroupService groupService;
    private final MaiParser parser;

    @Autowired
    public MaiUpdater(LessonService lessonService, ProfessorService professorService, GroupService groupService,
            MaiParser parser) {
        this.lessonService = lessonService;
        this.professorService = professorService;
        this.groupService = groupService;
        this.parser = parser;
    }

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
        saveDiff(diff);
        log.info("Finished update");
    }

    private void saveDiff(MaiDiff diff) {
        lessonService.saveAll(diff.getLessonsToSave());
        lessonService.saveAll(diff.getLessonsToMarkRemoved());
    }

    private MaiTimetable getOldTimetable() {
        Collection<Lesson> lessons = lessonService.bulkFindAllWithAllFields();
        Collection<Professor> professors = professorService.findAll();
        Collection<Group> groups = groupService.findAll();
        return new MaiTimetable(lessons, professors, groups);
    }

    private MaiDiff merge(MaiTimetable oldTimetable, MaiTimetable newTimetable) {
        Map<Long, Lesson> allLessonsFromOld = oldTimetable.getLessons().stream()
                .collect(Collectors.toMap(Lesson::getHash, l -> l));
        Map<String, Group> allGroupsFromOld = oldTimetable.getGroups().stream()
                .collect(Collectors.toMap(Group::getName, gr -> gr));
        Map<String, Professor> allProfessorsFromOld = oldTimetable.getProfessors().stream()
                .collect(Collectors.toMap(Professor::getFullName, pr -> pr));
        Map<Long, Lesson> allLessonsFromNew = newTimetable.getLessons().stream()
                .collect(Collectors.toMap(Lesson::getHash, l -> l));

        Map<Long, Lesson> newLessons = newTimetable.getLessons().stream()
                .filter(l -> !(allLessonsFromOld.containsKey(l.getHash())
                        && allLessonsFromOld.get(l.getHash()).getStatus().equals(LessonStatus.SAVED)))
                .collect(Collectors.toMap(Lesson::getHash, l -> l));
        List<Lesson> lessonsToSave = new ArrayList<>();
        for (Lesson lesson : newLessons.values()) {
            Lesson copy = Lesson.copyOf(lesson);
            Set<Group> newLessonGroups = new HashSet<>(copy.getGroups().size());
            for (Group group : copy.getGroups()) {
                newLessonGroups.add(allGroupsFromOld.get(group.getName()));
            }
            copy.setGroups(newLessonGroups);

            Set<Professor> newLessonProfessors = new HashSet<>(copy.getProfessors().size());
            for (Professor professor : copy.getProfessors()) {
                newLessonProfessors.add(allProfessorsFromOld.get(professor.getFullName()));
            }
            copy.setProfessors(newLessonProfessors);

            lessonsToSave.add(copy);
        }

        Map<Long, Lesson> lessonsToRemove = oldTimetable.getLessons().stream()
                .filter(l -> l.getStatus().equals(LessonStatus.SAVED))
                .filter(l -> !allLessonsFromNew.containsKey(l.getHash()))
                .collect(Collectors.toMap(Lesson::getHash, l -> l));
        lessonsToRemove.forEach((k, v) -> v.setStatus(LessonStatus.CANCELLED));

        return new MaiDiff(lessonsToRemove.values(), lessonsToSave);
    }
}