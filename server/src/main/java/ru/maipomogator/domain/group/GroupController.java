package ru.maipomogator.domain.group;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.maipomogator.domain.Views;
import ru.maipomogator.domain.lesson.Lesson;
import ru.maipomogator.domain.lesson.LessonService;

@Log4j2
@RestController
@RequestMapping("/mai/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final LessonService lessonService;

    @GetMapping
    @JsonView(Views.IdInfo.class)
    public List<Group> getAll() {
        return groupService.findAll();
    }

    @GetMapping(params = { "course", "faculty" })
    @JsonView(Views.IdInfo.class)
    public List<Group> getByCourseAndFaculty(
            @RequestParam(name = "course") Integer course,
            @RequestParam(name = "faculty") Integer faculty) {
        return groupService.findByCourseAndFaculty(course, faculty);
    }

    @GetMapping(params = { "course", "faculty", "type" })
    @JsonView(Views.IdInfo.class)
    public List<Group> getByCourseAndFacultyAndType(
            @RequestParam(name = "course", required = false) Integer course,
            @RequestParam(name = "faculty", required = false) Integer faculty,
            @RequestParam(name = "type", required = false) String type)
            throws BadRequestException {
        try {
            GroupType groupType = GroupType.valueOf(type);
            return groupService.findByCourseAndFacultyAndType(course, faculty, groupType);
        } catch (IllegalArgumentException iae) {
            throw new BadRequestException(iae);
        }
    }

    @GetMapping(params = "name")
    @JsonView(Views.IdInfo.class)
    public List<Group> getByName(Group example) {
        return groupService.findByExample(example);
    }

    @GetMapping("{id}")
    @JsonView(Views.IdInfo.class)
    public Group getOneById(@PathVariable("id") Group group) {
        return group;
    }

    @GetMapping("{id}/lessons")
    @JsonView(Views.FullView.class)
    public Collection<Lesson> getLessons(
            @PathVariable("id") Group group,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
            throws BadRequestException {

        startDate = (startDate == null) ? LocalDate.now() : startDate;
        endDate = (endDate == null) ? startDate.plusDays(7) : endDate;
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate must be before or equal to endDate");
        }

        if (Boolean.FALSE.equals(group.getIsActive())) {
            log.warn("Sending lessons for group {} that disappeared from site", group.getName());
        }

        return lessonService.findEagerForGroupBetweenDates(group, startDate, endDate);
    }
}
