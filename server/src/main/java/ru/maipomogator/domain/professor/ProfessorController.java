package ru.maipomogator.domain.professor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.domain.lesson.LessonDTO;
import ru.maipomogator.domain.lesson.LessonService;
import ru.maipomogator.exceptions.BadRequestException;
import ru.maipomogator.exceptions.NotFoundException;

@RestController
@RequestMapping("/api/v2/mai/professors")
@RequiredArgsConstructor
public class ProfessorController {
    private final ProfessorService professorService;
    private final LessonService lessonService;

    @GetMapping()
    public List<ProfessorDTO> getAll() {
        ResponseEntity.notFound().build();
        return professorService.findAllDTO();
    }

    @GetMapping(params = "fio")
    public List<ProfessorDTO> getByName(@RequestParam String fio) {
        return professorService.findByFioDTO(fio);
    }

    @GetMapping("{id}")
    public ProfessorDTO getOneById(@PathVariable("id") Long id) {
        return professorService.getOneByIdDTO(id).orElseThrow(NotFoundException::new);
    }

    @GetMapping("{id}/lessons")
    public Collection<LessonDTO> getLessons(@PathVariable("id") Professor professor,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        startDate = (startDate == null) ? LocalDate.now() : startDate;
        endDate = (endDate == null) ? startDate.plusDays(7) : endDate;
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate must be before or equal to endDate");
        }

        return lessonService.findEagerForProfessorBetweenDatesDTO(professor, startDate, endDate);
    }
}
