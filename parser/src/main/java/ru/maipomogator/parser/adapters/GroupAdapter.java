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
                    int facNumber = Integer.parseInt(in.nextString().replaceAll("\\D", ""));
                    gr.setFaculty(facNumber);
                    break;
                case "level":
                    gr.setType(getType(in.nextString()));
                    break;
                case "course":
                    gr.setCourse(in.nextInt());
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
