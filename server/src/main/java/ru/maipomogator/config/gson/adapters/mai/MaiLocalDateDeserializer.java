package ru.maipomogator.config.gson.adapters.mai;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import ru.maipomogator.config.gson.adapters.TypeableAdapter;
import ru.maipomogator.domain.mai.elements.MaiLocalDate;

@Component
public class MaiLocalDateDeserializer implements JsonDeserializer<MaiLocalDate>, TypeableAdapter {
    private static final DateTimeFormatter MAI_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public MaiLocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonPrimitive()) {
            throw new JsonParseException("Invalid JSON format for LocalDate object");
        }

        return new MaiLocalDate(LocalDate.parse(json.getAsString(), MAI_DATE_FORMAT));
    }

    @Override
    public Type getTargetType() {
        return MaiLocalDate.class;
    }

}
