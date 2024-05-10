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

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.maipomogator.domain.lesson.LessonDTO;
import ru.maipomogator.domain.lesson.LessonService;
import ru.maipomogator.exceptions.NotFoundException;

@Log4j2
@RestController
@RequestMapping("/api/v2/mai/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final LessonService lessonService;

    @GetMapping
    public List<GroupDTO> getAll() {
        return groupService.findAllDTO();
    }

    @GetMapping(params = { "course", "faculty" })
    public List<GroupDTO> getByCourseAndFaculty(
            @RequestParam(name = "course") Integer course,
            @RequestParam(name = "faculty") Integer faculty) {
        return groupService.findByCourseAndFacultyDTO(course, faculty);
    }

    @GetMapping(params = { "course", "faculty", "type" })
    public List<GroupDTO> getByCourseAndFacultyAndType(
            @RequestParam(name = "course", required = false) Integer course,
            @RequestParam(name = "faculty", required = false) Integer faculty,
            @RequestParam(name = "type", required = false) String type)
            throws BadRequestException {
        try {
            GroupType groupType = GroupType.valueOf(type);
            return groupService.findByCourseAndFacultyAndTypeDTO(course, faculty, groupType);
        } catch (IllegalArgumentException iae) {
            throw new BadRequestException(iae);
        }
    }

    @GetMapping(params = "name")
    public List<GroupDTO> getByName(@RequestParam String name) {
        return groupService.findByNameDTO(name);
    }

    @GetMapping("{id}")
    public GroupDTO getOneById(@PathVariable("id") Long id) {
        return groupService.getOneByIdDTO(id).orElseThrow(NotFoundException::new);
    }

    @GetMapping("{id}/lessons")
    public Collection<LessonDTO> getLessons(
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

        return lessonService.findEagerForGroupBetweenDatesDTO(group, startDate, endDate);
    }
}
