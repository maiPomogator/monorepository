package ru.maipomogator.bot.timetable.formatters;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ru.maipomogator.bot.timetable.TimetableTarget;

@Component
public class LessonFormatterSelector {
    private final Map<TimetableTarget, LessonFormatter> formatters;

    public LessonFormatterSelector(Collection<LessonFormatter> formatters) {
        this.formatters = formatters.stream()
                .collect(Collectors.toMap(LessonFormatter::getTarget, Function.identity()));
    }

    public LessonFormatter select(TimetableTarget target) {
        return formatters.get(target);
    }
}
