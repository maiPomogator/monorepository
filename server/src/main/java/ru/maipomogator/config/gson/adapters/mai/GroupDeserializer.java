package ru.maipomogator.config.gson.adapters.mai;

import java.lang.reflect.Type;

import org.springframework.stereotype.Component;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.config.gson.adapters.TypeableAdapter;
import ru.maipomogator.domain.group.Group;
import ru.maipomogator.domain.group.GroupType;

@Log4j2
@Component
public class GroupDeserializer implements JsonDeserializer<Group>, TypeableAdapter {

    @Override
    public Group deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        log.debug("Start deserializing group");

        if (!json.isJsonObject()) {
            log.error("Expected JsonObject but got {}", json);
            throw new JsonParseException("Invalid JSON format for Group object");
        }

        JsonObject jsonObject = json.getAsJsonObject();
        Group gr = new Group();

        if (jsonObject.has("name") && !jsonObject.get("name").isJsonNull()) {
            gr.setName(jsonObject.get("name").getAsString());
        }
        try {
            if (jsonObject.has("fac") && !jsonObject.get("fac").isJsonNull()) {
                String tempFac = jsonObject.get("fac").getAsString().replaceAll("\\D", "");
                gr.setFaculty(Integer.parseInt(tempFac));
            }

            if (jsonObject.has("level") && !jsonObject.get("level").isJsonNull()) {
                String tempLevel = jsonObject.get("level").getAsString();
                gr.setType(GroupType.getForName(tempLevel));
            }

            if (jsonObject.has("course") && !jsonObject.get("course").isJsonNull()) {
                String tempCourse = jsonObject.get("course").getAsString();
                gr.setCourse(Integer.parseInt(tempCourse));
            }
        } catch (IllegalArgumentException e) { // throw JsonParseException with group name
            log.error("Error while deserializing group: {}", gr.getName());
            throw new JsonParseException("Error while deserializing group: " + gr.getName(), e);
        }

        log.debug("Group deserialized: {}", gr);
        return gr;
    }

    @Override
    public Type getTargetType() {
        return Group.class;
    }
}
