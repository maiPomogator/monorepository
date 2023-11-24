package ru.maipomogator.parser.adapters;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.model.Lesson;
import ru.maipomogator.model.LessonType;
import ru.maipomogator.model.Professor;

@Log4j2
public class ParsedGroupAdapter extends TypeAdapter<ParsedGroup> {

    /**
     * регулярное выражение, соответствующее времени в формате HH:mm:ss например 10:45:00
     */
    private static final String TIME_REGEX = "\\d?\\d:\\d\\d:\\d\\d";
    /**
     * регулярное выражение, соответствующее дате в формате dd.MM.yyyy например 05.09.2022
     */
    private static final String DATE_REGEX = "\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d";

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm:ss");

    @Override
    public ParsedGroup read(JsonReader in) throws IOException {
        log.debug("Start deserializing lessons");
        // используется для исключения дублирования преподавателей с одинаковыми UUID внутри одной группы
        Map<UUID, Professor> groupProfessors = new HashMap<>();
        ParsedGroup parsedGroup = new ParsedGroup();

        String jsonKey;
        in.beginObject(); // желтая скобка (root)
        while (in.hasNext()) {
            jsonKey = in.nextName();
            if (jsonKey.equals("group")) {
                parsedGroup.setGroupName(in.nextString());
            } else if (jsonKey.matches(DATE_REGEX)) {
                LocalDate currentDate = LocalDate.parse(jsonKey, dateFormat);
                in.beginObject(); // фиолетовая скобка (после даты)
                while (in.hasNext()) {
                    jsonKey = in.nextName();
                    if (jsonKey.equals("day")) {
                        in.skipValue(); // пропускаем название дня недели, указанное в JSON`е
                    } else if (jsonKey.equals("pairs")) {
                        in.beginObject(); // синяя скобка (внутри 'pairs')
                        while (in.hasNext()) {
                            jsonKey = in.nextName();
                            if (jsonKey.matches(TIME_REGEX)) {
                                in.beginObject(); // желтая скобка (конкретная пара, перед названием)
                                // следующий цикл - костыль для случаев, когда в исходных файлах на одно время указано
                                // несколько пар, приходится их все также сохранять на одно время 😡
                                while (in.hasNext()) {
                                    Lesson newLesson = new Lesson();
                                    newLesson.setDay(currentDate);
                                    newLesson.setName(in.nextName());
                                    in.beginObject(); // фиолетовая скобка (конкретная пара, после названия)
                                    while (in.hasNext()) {
                                        switch (in.nextName()) {
                                            case "time_start":
                                                log.trace("Parsing time_start");
                                                newLesson.setTimeStart(LocalTime.parse(in.nextString(), timeFormat));
                                                break;
                                            case "time_end":
                                                log.trace("Parsing time_end");
                                                newLesson.setTimeEnd(LocalTime.parse(in.nextString(), timeFormat));
                                                break;
                                            case "lector":
                                                log.trace("Parsing lectors");
                                                in.beginObject(); // синяя скобка (внутри 'lector')
                                                while (in.hasNext()) {
                                                    UUID uuid = UUID.fromString(in.nextName());
                                                    if (groupProfessors.containsKey(uuid)) {
                                                        log.trace("Found already parsed professor with uuid " + uuid);
                                                        newLesson.addProfessor(groupProfessors.get(uuid));
                                                        in.skipValue();
                                                    } else {
                                                        log.trace("Found new professor with uuid " + uuid);
                                                        Professor pr = new Professor(uuid);
                                                        // в некоторых ФИО между словами два пробела
                                                        String profFio = in.nextString().replace("  ", " ");
                                                        String[] fioWords = profFio.split(" ");
                                                        if (fioWords.length == 3) {
                                                            pr.setLastName(fioWords[0]);
                                                            pr.setFirstName(fioWords[1]);
                                                            pr.setMiddleName(fioWords[2]);
                                                        } else {
                                                            // TODO сделать более продвинутое разбиение
                                                            pr.setLastName(profFio);
                                                        }
                                                        newLesson.addProfessor(pr);
                                                        groupProfessors.put(uuid, pr);
                                                    }
                                                }
                                                in.endObject(); // синяя скобка (внутри 'lector')
                                                break;
                                            case "type":
                                                log.trace("Parsing lesson types");
                                                in.beginObject(); // синяя скобка (внутри 'type')
                                                while (in.hasNext()) {
                                                    newLesson.addType(convertStringToLessonType(in.nextName()));
                                                    in.skipValue();
                                                }
                                                in.endObject(); // синяя скобка (внутри 'type')
                                                break;
                                            case "room":
                                                log.trace("Parsing rooms");
                                                in.beginObject(); // синяя скобка (внутри 'room')
                                                while (in.hasNext()) {
                                                    // пропускаем ключ, т.к. там UUID, который пока не используется
                                                    in.skipValue();
                                                    newLesson.addRoom(in.nextString());
                                                }
                                                in.endObject(); // синяя скобка (внутри 'room')
                                                break;
                                            case "lms", "teams", "other":
                                                log.trace("Skipping lms/teams/other");
                                                in.skipValue();
                                                break;
                                            default:
                                                log.warn(
                                                        "Encountered an unknown field inside the lesson. Skipping it.");
                                        }
                                    }
                                    in.endObject(); // фиолетовая скобка (конкретная пара, после названия)
                                    parsedGroup.add(newLesson);
                                } // конец костыля (см. сверху)
                                in.endObject(); // желтая скобка (конкретная пара, перед названием)
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
            } else {
                log.warn("Encountered an unknown field (neither 'group', nor date). Skipping it.");
            }
        }
        in.endObject(); // желтая скобка (root)
        log.debug("Returning {} lessons", parsedGroup.size());
        return parsedGroup;
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

    @Override
    public void write(JsonWriter out, ParsedGroup value) throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }
}
