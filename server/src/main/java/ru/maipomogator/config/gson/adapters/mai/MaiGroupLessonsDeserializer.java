package ru.maipomogator.config.gson.adapters.mai;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.config.gson.adapters.TypeableAdapter;
import ru.maipomogator.domain.lesson.Lesson;
import ru.maipomogator.domain.mai.elements.MaiGroupDay;
import ru.maipomogator.domain.mai.elements.MaiGroupLessons;
import ru.maipomogator.domain.mai.elements.MaiLocalDate;

@Log4j2
@Component
public class MaiGroupLessonsDeserializer implements JsonDeserializer<MaiGroupLessons.Modified>, TypeableAdapter {

    @Override
    public MaiGroupLessons.Modified deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        log.debug("Start deserializing group lessons");

        if (!json.isJsonObject()) {
            throw new JsonParseException("Invalid JSON format for GroupLessons object");
        }
        JsonObject jsonRoot = json.getAsJsonObject();

        JsonElement groupNameElement = jsonRoot.remove("group");
        if (groupNameElement == null) {
            throw new JsonParseException("Group name is missing");
        }
        String groupName = groupNameElement.getAsString();

        Map<MaiLocalDate, MaiGroupDay> days = context.deserialize(jsonRoot,
                new TypeToken<Map<MaiLocalDate, MaiGroupDay>>() {}.getType());
        Collection<Lesson> lessons = new ArrayList<>();

        days.forEach((date, day) -> {
            Collection<Lesson> dayLessons = day.getLessons();
            dayLessons.forEach(lesson -> lesson.setDate(date.date()));
            lessons.addAll(dayLessons);
        });

        return new MaiGroupLessons.Modified(groupName, lessons);
    }

    @Override
    public Type getTargetType() {
        return MaiGroupLessons.Modified.class;
    }
}
