package ru.maipomogator.config.gson.adapters.mai;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.stereotype.Component;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.config.gson.adapters.GsonAdapter;
import ru.maipomogator.domain.group.Group;
import ru.maipomogator.domain.group.GroupType;

@Log4j2

@Component
public class GroupAdapter extends TypeAdapter<Group> implements GsonAdapter {

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
                        gr.setType(null);
                    } else {
                        gr.setType(GroupType.getForName(tempLevel));
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

    @Override
    public void write(JsonWriter out, Group value) throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'write' in TypeAdapter<Group>");
    }

    @Override
    public Type getType() {
        return Group.class;
    }
}
