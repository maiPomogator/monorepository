package ru.maipomogator.domain.mai;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.domain.group.GroupService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MaiController {
    private final GroupService groupService;

    @GetMapping({ "/mai", "/api/v2/mai" })
    public MaiInfo getInfo() {
        List<String> faculties = groupService.findAllFaculties();
        Integer numberOfCourses = groupService.getNumberOfCourses();
        return new MaiInfo(faculties, numberOfCourses);
    }
}
