package ru.maipomogator.controllers;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.model.Group;
import ru.maipomogator.model.GroupType;
import ru.maipomogator.model.Lesson;
import ru.maipomogator.model.Views;
import ru.maipomogator.service.GroupService;
import ru.maipomogator.service.LessonService;

@RestController
@RequestMapping("/mai/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final LessonService lessonService;

    @GetMapping()
    @JsonView(Views.IdInfo.class)
    public ResponseEntity<List<Group>> getAll(@RequestParam(name = "course", required = false) Integer course,
            @RequestParam(name = "faculty", required = false) Integer faculty,
            @RequestParam(name = "type", required = false) String type) {
        if (course != null && faculty != null) {
            if (type != null) {
                try {
                    GroupType groupType = GroupType.valueOf(type);
                    return ResponseEntity
                            .ok(groupService.findByCourseAndFacultyAndType(course, faculty, groupType));
                } catch (IllegalArgumentException iae) {
                    return ResponseEntity.badRequest().build();
                }
            } else {
                return ResponseEntity.ok(groupService.findByCourseAndFaculty(course, faculty));
            }
        } else {
            return ResponseEntity.ok(groupService.findAll());
        }
    }

    @GetMapping("{id}")
    @JsonView(Views.IdInfo.class)
    public Group getOneById(@PathVariable("id") Group group) {
        return group;
    }

    @GetMapping(params = "name")
    @JsonView(Views.IdInfo.class)
    public Group getOneByName(@RequestParam String name) {
        return groupService.findByName(name);
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

        return lessonService.findEagerForGroupBetweenDates(group, startDate, endDate);
    }
}
