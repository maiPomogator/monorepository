package ru.maipomogator.domain.professor;

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
import ru.maipomogator.domain.lesson.LessonLegacyDTO;
import ru.maipomogator.domain.lesson.LessonService;
import ru.maipomogator.exceptions.BadRequestException;
import ru.maipomogator.exceptions.NotFoundException;

@Deprecated
@RestController
@RequestMapping("/mai/professors")
@RequiredArgsConstructor
public class ProfessorLegacyController {
    private final ProfessorService professorService;
    private final LessonService lessonService;

    @GetMapping()
    public List<ProfessorLegacyDTO> getAll() {
        return professorService.legacyFindAll();
    }

    @GetMapping(params = "fio")
    public List<ProfessorLegacyDTO> getByFio(String fio) {
        return professorService.legacyFindByFio(fio);
    }

    @GetMapping("{id}")
    public ProfessorLegacyDTO getOneById(@PathVariable("id") Long id) {
        return professorService.legacyGetOneById(id).orElseThrow(NotFoundException::new);
    }

    @GetMapping("{id}/lessons")
    public Collection<LessonLegacyDTO> getLessons(@PathVariable("id") Professor professor,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        startDate = (startDate == null) ? LocalDate.now() : startDate;
        endDate = (endDate == null) ? startDate.plusDays(7) : endDate;
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate must be before or equal to endDate");
        }

        return lessonService.legacyFindEagerForProfessorBetweenDates(professor, startDate, endDate);
    }
}
