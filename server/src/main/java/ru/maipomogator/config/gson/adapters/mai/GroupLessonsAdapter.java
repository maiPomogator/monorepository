package ru.maipomogator.config.gson.adapters.mai;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.config.gson.adapters.GsonAdapter;
import ru.maipomogator.domain.lesson.Lesson;
import ru.maipomogator.domain.lesson.LessonType;
import ru.maipomogator.domain.professor.Professor;

@Log4j2
@Component
public class GroupLessonsAdapter extends TypeAdapter<Collection<Lesson>> implements GsonAdapter {

    private static final Pattern DATE_PATTERN = Pattern.compile("\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d");
    private static final Pattern TIME_PATTERN = Pattern.compile("\\d?\\d:\\d\\d:\\d\\d");

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm:ss");

    private static final UUID ZERO_UUID = new UUID(0, 0);

    @Override
    public Collection<Lesson> read(JsonReader in) throws IOException {
        Matcher dateMatcher = DATE_PATTERN.matcher("");
        Collection<Lesson> lessons = new ArrayList<>();
        String groupName = "Имя не указано";

        in.beginObject(); // желтая скобка (root)
        String jsonKey;
        while (in.hasNext()) {
            jsonKey = in.nextName();
            if (jsonKey.equals("group")) {
                groupName = in.nextString();
            } else if (dateMatcher.reset(jsonKey).matches()) {
                LocalDate currentDate = LocalDate.parse(jsonKey, DATE_FORMAT);
                lessons.addAll(parseDay(currentDate, in));
            } else {
                log.warn("Encountered an unknown field (neither 'group', nor 'date'). Skipping it.");
            }

        }
        in.endObject(); // желтая скобка (root)

        log.debug("Returning {} lessons for group \"{}\"", lessons.size(), groupName);
        return lessons;
    }

    @Override
    public void write(JsonWriter out, Collection<Lesson> value) throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'write' in TypeAdapter<Set<Lesson>>");
    }

    @Override
    public Type getType() {
        return new TypeToken<Collection<Lesson>>() {}.getType();
    }

    private Collection<Lesson> parseDay(LocalDate currentDate, JsonReader in) throws IOException {
        Matcher timeMatcher = TIME_PATTERN.matcher("");
        Collection<Lesson> dayLessons = new HashSet<>();

        in.beginObject(); // фиолетовая скобка (после даты)
        while (in.hasNext()) {
            String jsonKey = in.nextName();
            if (jsonKey.equals("day")) {
                in.skipValue(); // пропускаем название дня недели, указанное в JSON`е
            } else if (jsonKey.equals("pairs")) {
                in.beginObject(); // синяя скобка (внутри 'pairs')
                while (in.hasNext()) {
                    jsonKey = in.nextName();
                    if (timeMatcher.reset(jsonKey).matches()) {
                        dayLessons.addAll(parseTimeLessons(currentDate, in));
                    } else {
                        log.error("Encountered an unknown field inside 'pairs' that is not time. Skipping it.");
                    }
                }
                in.endObject(); // синяя скобка (внутри 'pairs')
            } else {
                log.warn("Encountered an unknown field inside a day. Skipping it.");
            }
        }
        in.endObject(); // фиолетовая скобка (после даты)

        return dayLessons;
    }

    private Collection<Lesson> parseTimeLessons(LocalDate currentDate, JsonReader in) throws IOException {
        Collection<Lesson> timeLessons = new HashSet<>();

        in.beginObject(); // желтая скобка (конкретная пара, перед названием)
        while (in.hasNext()) {// цикл - костыль для случаев, когда в исходных файлах на одно время указано несколько пар
            timeLessons.add(parseLesson(currentDate, in));
        }
        in.endObject(); // желтая скобка (конкретная пара, перед названием)

        return timeLessons;
    }

    private Lesson parseLesson(LocalDate currentDate, JsonReader in) throws IOException {
        Lesson newLesson = new Lesson();
        newLesson.setDate(currentDate);
        newLesson.setName(in.nextName());

        in.beginObject(); // фиолетовая скобка (конкретная пара, после названия)
        while (in.hasNext()) {
            String jsonKey = in.nextName();
            log.trace("Parsing {}", jsonKey);
            switch (jsonKey) {
                case "time_start":
                    newLesson.setTimeStart(LocalTime.parse(in.nextString(), TIME_FORMAT));
                    break;
                case "time_end":
                    newLesson.setTimeEnd(LocalTime.parse(in.nextString(), TIME_FORMAT));
                    break;
                case "lector":
                    newLesson.setProfessors(parseProfessors(in));
                    break;
                case "type":
                    newLesson.setTypes(parseTypes(in));
                    break;
                case "room":
                    newLesson.setRooms(parseRooms(in));
                    break;
                case "lms", "teams", "other":
                    in.skipValue();
                    break;
                default:
                    log.warn("Encountered an unknown field inside the lesson. Skipping it.");
                    break;
            }
        }
        in.endObject(); // фиолетовая скобка (конкретная пара, после названия)

        return newLesson;
    }

    private Set<Professor> parseProfessors(JsonReader in) throws IOException {
        Set<Professor> lessonProfessors = new HashSet<>();

        in.beginObject(); // синяя скобка (внутри 'lector')
        while (in.hasNext()) {
            UUID siteId = UUID.fromString(in.nextName());
            if (siteId.equals(ZERO_UUID)) {
                log.trace("Skipped zero professor." + siteId);
                in.skipValue();
            } else {
                log.trace("Added professor with UUID=" + siteId);
                String fio = in.nextString().replace("  ", " "); // в некоторых ФИО между словами два пробела
                lessonProfessors.add(new Professor(siteId, fio));
            }
        }
        in.endObject(); // синяя скобка (внутри 'lector')

        return lessonProfessors;
    }

    private Set<LessonType> parseTypes(JsonReader in) throws IOException {
        Set<LessonType> lessonTypes = new HashSet<>();

        in.beginObject(); // синяя скобка (внутри 'type')
        while (in.hasNext()) {
            lessonTypes.add(convertStringToLessonType(in.nextName()));
            in.skipValue();
        }
        in.endObject(); // синяя скобка (внутри 'type')

        return lessonTypes;
    }

    private Set<String> parseRooms(JsonReader in) throws IOException {
        Set<String> lessonRooms = new HashSet<>();

        in.beginObject(); // синяя скобка (внутри 'room')
        while (in.hasNext()) {
            // пропускаем ключ, т.к. там UUID, который пока не используется
            in.skipValue();
            lessonRooms.add(in.nextString());
        }
        in.endObject(); // синяя скобка (внутри 'room')

        return lessonRooms;
    }

    private LessonType convertStringToLessonType(String lessonTypeStr) {
        return switch (lessonTypeStr.toUpperCase()) {
            case "ЛК" -> LessonType.LECTURE;
            case "ПЗ" -> LessonType.PRACTICE;
            case "ЛР" -> LessonType.LABORATORY;
            case "ЗАЧЕТ" -> LessonType.CREDIT;
            case "ЭКЗАМЕН" -> LessonType.EXAM;
            default -> throw new IllegalArgumentException("Unknown lesson type %s".formatted(lessonTypeStr));
        };
    }
}
