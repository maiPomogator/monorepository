package ru.maipomogator.updaters.mai.adapters;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.model.Group;

@Log4j2

@Component
public class GroupListAdapter extends TypeAdapter<List<Group>> implements GsonAdapter {

    @Override
    public List<Group> read(JsonReader in) throws IOException {
        log.debug("Start deserializing array of groups");
        List<Group> groups = new ArrayList<>();
        GroupAdapter groupAdapter = new GroupAdapter();
        
        in.beginArray();
        while (in.hasNext()) {
            Group group = groupAdapter.read(in);
            if (group.getCourse() != 0) {
                groups.add(group);
            } else {
                log.info("Group with empty course and faculty was dropped");
            }
        }
        in.endArray();

        log.debug("Deserialized {} groups", groups.size());
        return groups;
    }

    @Override
    public void write(JsonWriter out, List<Group> value) throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'write' in TypeAdapter<List<Group>>");
    }

    @Override
    public Type getType() {
        return new TypeToken<List<Group>>() {}.getType();
    }
}
