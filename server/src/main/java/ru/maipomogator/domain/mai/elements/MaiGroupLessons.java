package ru.maipomogator.domain.mai.elements;

import java.time.ZonedDateTime;
import java.util.Collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.maipomogator.domain.lesson.Lesson;

public sealed interface MaiGroupLessons {

    public static final class NotModified implements MaiGroupLessons {}

    @RequiredArgsConstructor
    @Getter
    public static final class Modified implements MaiGroupLessons {
        private final String groupName;
        private final Collection<Lesson> lessons;

        @Accessors(chain = true)
        @Setter
        private ZonedDateTime lastModified;
    }

    @RequiredArgsConstructor
    @Getter
    public static final class Failed implements MaiGroupLessons {
        private final String groupName;
        private final Throwable cause;
    }
}
