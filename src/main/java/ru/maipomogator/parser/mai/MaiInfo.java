package ru.maipomogator.parser.mai;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MaiInfo {
    private final List<String> faculties;
    private final Integer numberOfCourses;
}
