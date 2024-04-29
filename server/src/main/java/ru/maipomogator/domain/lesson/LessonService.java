package ru.maipomogator.domain.lesson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.domain.group.Group;
import ru.maipomogator.domain.professor.Professor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepo lessonRepo;
    @Deprecated
    private final LessonLegacyMapper legacyMapper;

    @Transactional
    public List<Lesson> saveAll(Iterable<Lesson> lessons) {
        return lessonRepo.saveAll(lessons);
    }

    public List<Lesson> eagerFindAllForGroups(Collection<Group> groups) {
        List<Long> lessonIds = lessonRepo.findLessonIdsByGroupIds(groups.stream().map(Group::getId).toList());
        if (lessonIds.isEmpty()) {
            return List.of();
        }
        List<Lesson> allLessons = new ArrayList<>();
        int batchSize = 65_535;
        for (int i = 0; i < lessonIds.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, lessonIds.size());
            List<Long> batchIds = lessonIds.subList(i, endIndex);
            allLessons.addAll(lessonRepo.findEagerByIdInOrderByDateAscTimeStartAsc(batchIds));
        }

        return allLessons;
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

    @Deprecated
    public List<LessonLegacyDTO> legacyFindEagerForGroupBetweenDates(Group group, LocalDate startDate,
            LocalDate endDate) {
        return legacyMapper.toLegacyDTOs(findEagerForGroupBetweenDates(group, startDate, endDate));
    }

    @Deprecated
    public List<LessonLegacyDTO> legacyFindEagerForProfessorBetweenDates(Professor professor, LocalDate startDate,
            LocalDate endDate) {
        return legacyMapper.toLegacyDTOs(findEagerForProfessorBetweenDates(professor, startDate, endDate));
    }
}
