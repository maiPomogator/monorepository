package ru.maipomogator.domain.mai;

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
import ru.maipomogator.domain.group.Group;
import ru.maipomogator.domain.lesson.Lesson;
import ru.maipomogator.domain.mai.elements.MaiGroupList;

@Log4j2
@Component
@RequiredArgsConstructor
public class MaiRestClient {
    private final RestClient restClient;

    public MaiGroupList getMaiGroupList() {
        return restClient
                .get()
                .uri("groups.json")
                .retrieve()
                .body(MaiGroupList.class);
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
