package ru.maipomogator.controllers;

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
import ru.maipomogator.model.Lesson;
import ru.maipomogator.model.Professor;
import ru.maipomogator.model.Views;
import ru.maipomogator.service.LessonService;
import ru.maipomogator.service.ProfessorService;

@RestController
@RequestMapping("/mai/professors")
@RequiredArgsConstructor
public class ProfessorController {
    private final ProfessorService professorService;
    private final LessonService lessonService;

    @GetMapping()
    @JsonView(Views.IdInfo.class)
    public List<Professor> getAll() {
        return professorService.findAll();
    }

    @GetMapping(params = "fio")
    @JsonView(Views.IdInfo.class)
    public List<Professor> getByFio(String fio) {
        return professorService.findByFio(fio);
    }

    @GetMapping("{id}")
    @JsonView(Views.IdInfo.class)
    public Professor getOneById(@PathVariable("id") Professor professor) {
        return professor;
    }

    @GetMapping("{id}/lessons")
    @JsonView(Views.FullView.class)
    public Collection<Lesson> getLessons(@PathVariable("id") Professor professor,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
            throws BadRequestException {

        startDate = (startDate == null) ? LocalDate.now() : startDate;
        endDate = (endDate == null) ? startDate.plusDays(7) : endDate;
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate must be before or equal to endDate");
        }

        return lessonService.findEagerForProfessorBetweenDates(professor, startDate, endDate);
    }
}
