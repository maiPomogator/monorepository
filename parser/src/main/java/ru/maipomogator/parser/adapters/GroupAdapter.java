package ru.maipomogator.parser.adapters;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.model.Group;
import ru.maipomogator.model.GroupType;

@Log4j2

public class GroupAdapter extends TypeAdapter<Group> {

    @Override
    public Group read(JsonReader in) throws IOException {
        log.debug("Start deserializing group");

        in.beginObject();
        Group gr = new Group();
        while (in.hasNext()) {
            String jsonKeyName = in.nextName();
            switch (jsonKeyName) {
                case "name":
                    gr.setName(in.nextString());
                    break;
                case "fac":
                    String tempFac = in.nextString().replaceAll("\\D", "");
                    if ("".equals(tempFac)) {
                        gr.setFaculty(0);
                    } else {
                        gr.setFaculty(Integer.parseInt(tempFac));
                    }
                    break;
                case "level":
                    String tempLevel = in.nextString();
                    if ("".equals(tempLevel)) {
                        gr.setType(GroupType.BACHELOR);
                    } else {
                        gr.setType(getType(tempLevel));
                    }
                    break;
                case "course":
                    String tempCourse = in.nextString();
                    if ("".equals(tempCourse)) {
                        gr.setCourse(0);
                    } else {
                        gr.setCourse(Integer.parseInt(tempCourse));
                    }
                    break;
                default:
                    log.warn("Unknown field {}, skipping", jsonKeyName);
                    in.skipValue();
                    break;
            }
        }
        in.endObject();
        log.debug("Group deserialized: {}", gr);
        return gr;
    }

    private GroupType getType(String groupTypeStr) {
        return switch (groupTypeStr) {
            case "Бакалавриат" -> GroupType.BACHELOR;
            case "Магистратура" -> GroupType.MAGISTRACY;
            case "Аспирантура" -> GroupType.POSTGRADUATE;
            case "Специалитет" -> GroupType.SPECIALIST;
            case "Базовое высшее образование" -> GroupType.BASIC_HIGHER_EDUCATION;
            case "Специализированное высшее образование" -> GroupType.SPECIALIZED_HIGHER_EDUCATION;
            default -> throw new IllegalArgumentException("Unknown group type %s".formatted(groupTypeStr));
        };
    }

    @Override
    public void write(JsonWriter out, Group value) throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }
}
