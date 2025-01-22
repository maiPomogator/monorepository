package ru.maipomogator.config.gson.adapters.mai;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ru.maipomogator.config.gson.adapters.TypeableAdapter;
import ru.maipomogator.domain.mai.elements.MaiLocalTime;

/**
 * Костыль для десериализации времени в ответах API МАИ в формате "H:mm:ss". 
 */
@Component
public class MaiLocalTimeDeserializer
        implements JsonSerializer<MaiLocalTime>, JsonDeserializer<MaiLocalTime>, TypeableAdapter {


    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("H:mm:ss");

    @Override
    public JsonElement serialize(MaiLocalTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(FORMATTER.format(src.time()));
    }

    @Override
    public MaiLocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return new MaiLocalTime(FORMATTER.parse(json.getAsString(), LocalTime::from));
    }

    @Override
    public Type getTargetType() {
        return MaiLocalTime.class;
    }
}
