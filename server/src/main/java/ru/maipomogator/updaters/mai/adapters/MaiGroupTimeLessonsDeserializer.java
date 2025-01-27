package ru.maipomogator.updaters.mai.adapters;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.config.gson.TypeableAdapter;
import ru.maipomogator.domain.lesson.LessonType;
import ru.maipomogator.domain.professor.Professor;
import ru.maipomogator.updaters.mai.elements.MaiGroupLesson;
import ru.maipomogator.updaters.mai.elements.MaiGroupTimeLessons;
import ru.maipomogator.updaters.mai.elements.MaiLocalTime;

@Log4j2
@Component
public class MaiGroupTimeLessonsDeserializer implements JsonDeserializer<MaiGroupTimeLessons>, TypeableAdapter {

    @Override
    public MaiGroupTimeLessons deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("Invalid JSON format for GroupLesson object");
        }
        Set<Entry<String, JsonElement>> lessons = json.getAsJsonObject().entrySet();
        Collection<MaiGroupLesson> result = new ArrayList<>();
        for (Entry<String, JsonElement> entry : lessons) {
            result.add(parseLesson(entry.getKey(), entry.getValue(), context));
        }

        return new MaiGroupTimeLessons(result);
    }

    @Override
    public Type getTargetType() {
        return MaiGroupTimeLessons.class;
    }

    /**
     * Сделано для того, чтобы не влиять на сериализацию и десериализацию Lesson
     */
    private MaiGroupLesson parseLesson(String name, JsonElement json, JsonDeserializationContext context) {
        log.debug("Start deserializing lesson");

        if (!json.isJsonObject()) {
            log.error("Expected JsonObject but got {}", json);
            throw new JsonParseException("Invalid JSON format for Lesson object");
        }

        JsonObject jsonObject = json.getAsJsonObject();
        MaiGroupLesson lesson = new MaiGroupLesson();
        lesson.setName(name);

        lesson.setTimeStart(parseTime(jsonObject.get("time_start"), context));
        lesson.setTimeEnd(parseTime(jsonObject.get("time_end"), context));
        lesson.setProfessors(parseProfessors(jsonObject.get("lector"), context));
        lesson.setTypes(parseTypes(jsonObject.get("type"), context));
        lesson.setRooms(parseRooms(jsonObject.get("room"), context));

        return lesson;
    }

    private LocalTime parseTime(JsonElement json, JsonDeserializationContext context) {
        return ((MaiLocalTime) context.deserialize(json, MaiLocalTime.class)).time();
    }

    private Set<String> parseRooms(JsonElement rooms, JsonDeserializationContext context) {
        if (!rooms.isJsonObject()) {
            throw new JsonParseException("Invalid JSON format for Rooms object");
        }

        // UUID -> room name
        Map<UUID, String> roomsMap = context.deserialize(rooms, new TypeToken<Map<UUID, String>>() {}.getType());

        return roomsMap.values().stream().collect(Collectors.toSet());
    }

    private Set<LessonType> parseTypes(JsonElement types, JsonDeserializationContext context) {
        if (!types.isJsonObject()) {
            throw new JsonParseException("Invalid JSON format for Types object");
        }

        // type -> some 1 value
        Map<String, String> typesMap = context.deserialize(types, new TypeToken<Map<String, String>>() {}.getType());

        return typesMap.entrySet().stream()
                .map(entry -> convertStringToLessonType(entry.getKey()))
                .collect(Collectors.toSet());
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

    private Set<Professor> parseProfessors(JsonElement professors, JsonDeserializationContext context) {
        if (!professors.isJsonObject()) {
            throw new JsonParseException("Invalid JSON format for Professors object");
        }
        // siteId -> fio
        Map<UUID, String> professorsMap = context.deserialize(professors,
                new TypeToken<Map<UUID, String>>() {}.getType());

        return professorsMap.entrySet().stream()
                .map(entry -> new Professor(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }
}
