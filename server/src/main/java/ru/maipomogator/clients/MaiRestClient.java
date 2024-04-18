package ru.maipomogator.clients;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.maipomogator.model.Group;
import ru.maipomogator.model.Lesson;

@Log4j2
@Component
@RequiredArgsConstructor
public class MaiRestClient {
    private final RestClient restClient;
    private final Gson gson;

    public Collection<Group> getAllGroups() {
        String groupsString = restClient.get().uri("groups.json").retrieve().body(String.class);
        if (groupsString == null || groupsString.isBlank()) {
            log.warn("MAI groups.json is empty.");
            return List.of();
        }
        return gson.fromJson(groupsString, new TypeToken<List<Group>>() {});
    }

    public Collection<Lesson> getLessonsForGroup(Group group) {
        String fileName = DigestUtils.md5DigestAsHex(group.getName().getBytes(StandardCharsets.UTF_8)) + ".json";
        String groupData = restClient.get().uri(fileName).retrieve().body(String.class);
        if (groupData == null) {
            return List.of();
        }

        String groupDataHash = DigestUtils.md5DigestAsHex(groupData.getBytes(StandardCharsets.UTF_8));
        if (groupDataHash.equals(group.getLatestHash())) {
            return List.of();
        }

        Collection<Lesson> lessons = gson.fromJson(groupData, new TypeToken<>() {});
        lessons.forEach(l -> l.addGroup(group));
        group.setLatestHash(groupDataHash);
        return lessons;
    }
}
