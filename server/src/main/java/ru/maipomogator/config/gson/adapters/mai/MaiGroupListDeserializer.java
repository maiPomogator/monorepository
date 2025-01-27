package ru.maipomogator.config.gson.adapters.mai;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import ru.maipomogator.domain.mai.elements.MaiGroupList;

@Log4j2
@Component
/**
 * Десериализатор для ответа API МАИ, содержащего список групп
 */
public class MaiGroupListDeserializer implements JsonDeserializer<MaiGroupList>, TypeableAdapter {

    @Override
    public MaiGroupList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        log.debug("Start deserializing MAI group list");
        List<Group> groups = new ArrayList<>();

        if (!json.isJsonArray()) {
            throw new JsonParseException("Invalid JSON format for GroupList object");
        }
        for (JsonElement jsonElement : json.getAsJsonArray()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Group gr = new Group();

            gr.setName(jsonObject.get("name").getAsString());

            try {
                // TODO replace by group.name parsing
                if (jsonObject.has("fac") && !jsonObject.get("fac").isJsonNull()) {
                    String tempFac = jsonObject.get("fac").getAsString().replaceAll("\\D", "");
                    gr.setFaculty(Integer.parseInt(tempFac));
                }

                if (jsonObject.has("level") && !jsonObject.get("level").isJsonNull()) {
                    String tempLevel = jsonObject.get("level").getAsString();
                    gr.setType(parseGroupType(tempLevel));
                }

                if (jsonObject.has("course") && !jsonObject.get("course").isJsonNull()) {
                    String tempCourse = jsonObject.get("course").getAsString();
                    gr.setCourse(Integer.parseInt(tempCourse));
                }
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Error while deserializing group: " + gr.getName(), e);
            }
            groups.add(gr);
        }

        log.debug("Deserialized {} groups", groups.size());
        return new MaiGroupList(groups);
    }

    @Override
    public Type getTargetType() {
        return MaiGroupList.class;
    }

    private GroupType parseGroupType(String typeName) throws IllegalArgumentException {
        return Arrays.stream(GroupType.values())
                .filter(groupType -> groupType.getName().equalsIgnoreCase(typeName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown GroupType: " + typeName));
    }
}
