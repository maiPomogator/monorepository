package ru.maipomogator.updaters.mai.adapters;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import ru.maipomogator.config.gson.TypeableAdapter;
import ru.maipomogator.updaters.mai.elements.MaiLocalTime;

/**
 * Костыль для десериализации времени в ответах API МАИ в формате "H:mm:ss".
 */
@Component
public class MaiLocalTimeDeserializer
        implements JsonDeserializer<MaiLocalTime>, TypeableAdapter {
    private static final DateTimeFormatter MAI_TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm:ss");

    @Override
    public MaiLocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return new MaiLocalTime(LocalTime.parse(json.getAsString(), MAI_TIME_FORMAT));
    }

    @Override
    public Type getTargetType() {
        return MaiLocalTime.class;
    }
}
