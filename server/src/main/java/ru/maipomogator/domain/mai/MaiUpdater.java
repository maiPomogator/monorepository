package ru.maipomogator.domain.mai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.maipomogator.domain.group.Group;
import ru.maipomogator.domain.group.GroupService;
import ru.maipomogator.domain.lesson.Lesson;
import ru.maipomogator.domain.lesson.LessonService;
import ru.maipomogator.domain.mai.elements.MaiGroupLessons;
import ru.maipomogator.domain.professor.Professor;
import ru.maipomogator.domain.professor.ProfessorService;

@RequiredArgsConstructor
@Log4j2
@Component
public class MaiUpdater {

    private final GroupService groupService;
    private final ProfessorService professorService;
    private final LessonService lessonService;

    private final MaiRestClient maiRestClient;
    private final MaiUpdaterConfig config;

    @Transactional
    @Scheduled(cron = "0 0 9 * * *")
    public void updateScheduled() {
        update();
    }

    public void update() {
        Collection<Group> groupsFromMAI = maiRestClient.getMaiGroupList();
        if (groupsFromMAI == null || groupsFromMAI.isEmpty()) {
            log.info("No groups from MAI. Returning.");
            return;
        }

        log.info("Got {} groups from MAI.", groupsFromMAI.size());
        update2(groupsFromMAI);
    }

    private void update2(Collection<Group> groupsFromMAI) {
        Collection<Group> groupsFromDB = groupService.findAll();
        log.info("Got {} groups from db", groupsFromDB.size());

        Set<Group> commonGroups = new HashSet<>(groupsFromDB);
        commonGroups.retainAll(groupsFromMAI);

        Set<Group> missingGroups = new HashSet<>(groupsFromDB);
        missingGroups.removeAll(groupsFromMAI);
        if (!missingGroups.isEmpty()) {
            missingGroups.forEach(Group::deactivate);
            groupService.saveAll(missingGroups);
        }
        if (config.includeMissingGroups()) {
            commonGroups.addAll(missingGroups);
        }

        Set<Group> newGroups = new HashSet<>(groupsFromMAI);
        newGroups.removeAll(groupsFromDB);
        if (!newGroups.isEmpty()) {
            Collection<Group> savedGroups = groupService.saveAll(newGroups);
            commonGroups.addAll(savedGroups);
        }

        log.info("commonGroups: {}, missingGroups: {}, newGroups: {}",
                commonGroups.size(), missingGroups.size(), newGroups.size());

        Map<Group, Collection<Lesson>> changedGroups = new HashMap<>();
        for (Group group : commonGroups) {
            MaiGroupLessons groupLessons = maiRestClient.getMaiGroupLessons(group.getName(), group.getLastModified());
            if (groupLessons instanceof MaiGroupLessons.Modified modified) {
                group.setLastModified(modified.getLastModified());
                Collection<Lesson> lessonsFromMAI = modified.getLessons();
                changedGroups.put(group, lessonsFromMAI);
                if (lessonsFromMAI.isEmpty()) {
                    log.debug("Skipping group {}", group.getName());
                } else {
                    changedGroups.put(group, lessonsFromMAI);
                    log.info("Got {} lessons from MAI for group {}", lessonsFromMAI.size(), group.getName());
                }
            }
        }

        if (changedGroups.isEmpty()) {
            log.info("No changes for all groups. Returning.");
            return;
        }

        update3(changedGroups);
    }

    private void update3(Map<Group, Collection<Lesson>> changedGroups) {
        Map<UUID, Professor> professorsFromDB = professorService.findAll().stream()
                .collect(Collectors.toMap(Professor::getSiteId, Function.identity()));
        Map<Lesson, Lesson> mapLessons = new HashMap<>(300_000);
        Collection<Lesson> allLessonsFromDB = lessonService.eagerFindAllForGroups(changedGroups.keySet());
        changedGroups.forEach((group, lessonsFromMAI) -> {
            Collection<Lesson> lessonsFromDB = allLessonsFromDB.stream().filter(l -> l.getGroups().contains(group))
                    .toList();

            Collection<Lesson> commonLessons = new ArrayList<>(lessonsFromDB);
            commonLessons.retainAll(lessonsFromMAI);
            Collection<Lesson> lessonsToActivate = commonLessons.stream()
                    .filter(((Predicate<Lesson>) Lesson::isActive).negate()).toList();
            if (!lessonsToActivate.isEmpty()) {
                lessonsToActivate.forEach(Lesson::activate);
                lessonService.saveAll(lessonsToActivate);
            }

            Collection<Lesson> missingLessons = new ArrayList<>(lessonsFromDB);
            missingLessons.removeAll(lessonsFromMAI);
            missingLessons.forEach(Lesson::deactivate);
            if (!missingLessons.isEmpty()) {
                lessonService.saveAll(missingLessons);
            }

            Collection<Lesson> newLessons = new ArrayList<>(lessonsFromMAI);
            newLessons.removeAll(lessonsFromDB);
            newLessons = newLessons.stream().map(rawLesson -> mapLessons.computeIfAbsent(rawLesson, lesson -> {
                Set<Professor> newProfessors = lesson.getProfessors().stream().map(newProfessor -> {
                    if (!professorsFromDB.containsKey(newProfessor.getSiteId())) {
                        Professor saved = professorService.save(newProfessor);
                        professorsFromDB.put(saved.getSiteId(), saved);
                    }
                    return professorsFromDB.get(newProfessor.getSiteId());
                })
                        .collect(Collectors.toSet());
                lesson.setProfessors(newProfessors);
                return lesson;
            })).toList();
            if (!newLessons.isEmpty()) {
                lessonService.saveAll(newLessons);
            }

            groupService.save(group);
        });

        log.info("{} groups updated", changedGroups.size());
    }
}
