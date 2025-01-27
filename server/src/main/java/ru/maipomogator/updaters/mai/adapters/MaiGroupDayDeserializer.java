package ru.maipomogator.updaters.mai.adapters;

import java.lang.reflect.Type;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import ru.maipomogator.config.gson.TypeableAdapter;
import ru.maipomogator.updaters.mai.elements.MaiGroupDay;
import ru.maipomogator.updaters.mai.elements.MaiGroupTimeLessons;
import ru.maipomogator.updaters.mai.elements.MaiLocalTime;

@Component
public class MaiGroupDayDeserializer implements JsonDeserializer<MaiGroupDay>, TypeableAdapter {

    @Override
    public MaiGroupDay deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("Invalid JSON format for GroupDay object");
        }

        JsonObject jsonObject = json.getAsJsonObject();
        jsonObject.remove("day");

        return new MaiGroupDay(
                context.deserialize(jsonObject.get("pairs"), new TypeToken<Map<MaiLocalTime, MaiGroupTimeLessons>>() {}.getType()));
    }

    @Override
    public Type getTargetType() {
        return MaiGroupDay.class;
    }
}
