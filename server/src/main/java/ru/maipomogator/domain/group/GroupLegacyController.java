package ru.maipomogator.domain.group;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.maipomogator.domain.lesson.LessonLegacyDTO;
import ru.maipomogator.domain.lesson.LessonService;
import ru.maipomogator.exceptions.BadRequestException;
import ru.maipomogator.exceptions.NotFoundException;

@Deprecated
@Log4j2
@RestController
@RequestMapping("/mai/groups")
@RequiredArgsConstructor
public class GroupLegacyController {
    private final GroupService groupService;
    private final LessonService lessonService;

    @GetMapping
    public List<GroupLegacyDTO> getAll() {
        return groupService.legacyFindAll();
    }

    @GetMapping(params = { "course", "faculty" })
    public List<GroupLegacyDTO> getByCourseAndFaculty(
            @RequestParam(name = "course") Integer course,
            @RequestParam(name = "faculty") Integer faculty) {
        return groupService.legacyFindByCourseAndFaculty(course, faculty);
    }

    @GetMapping(params = { "course", "faculty", "type" })
    public List<GroupLegacyDTO> getByCourseAndFacultyAndType(
            @RequestParam(name = "course", required = false) Integer course,
            @RequestParam(name = "faculty", required = false) Integer faculty,
            @RequestParam(name = "type", required = false) String type) {
        try {
            GroupType groupType = GroupType.valueOf(type);
            return groupService.legacyFindByCourseAndFacultyAndType(course, faculty, groupType);
        } catch (IllegalArgumentException iae) {
            throw new BadRequestException("Probably bad type='%s' for GroupType".formatted(type), iae);
        }
    }

    @GetMapping(params = "name")
    public List<GroupLegacyDTO> getByName(@RequestParam String name) {
        return groupService.legacyFindByExample(name);
    }

    @GetMapping("{id}")
    public GroupLegacyDTO getOneById(@PathVariable("id") Long id) {
        return groupService.legacyGetOneById(id).orElseThrow(NotFoundException::new);
    }

    @GetMapping("{id}/lessons")
    public Collection<LessonLegacyDTO> getLessons(
            @PathVariable("id") Group group,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        startDate = (startDate == null) ? LocalDate.now() : startDate;
        endDate = (endDate == null) ? startDate.plusDays(7) : endDate;
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate must be before or equal to endDate");
        }

        if (Boolean.FALSE.equals(group.getIsActive())) {
            log.warn("Sending lessons for group {} that disappeared from site", group.getName());
        }

        return lessonService.legacyFindEagerForGroupBetweenDates(group, startDate, endDate);
    }
}
