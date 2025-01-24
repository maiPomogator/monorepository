package ru.maipomogator.domain.mai;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.google.gson.JsonParseException;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.domain.mai.elements.MaiGroupLessons;
import ru.maipomogator.domain.mai.elements.MaiGroupList;

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

    public MaiGroupLessons getMaiGroupLessons(String groupName, ZonedDateTime lastModified) {
        String groupNameMd5 = DigestUtils.md5DigestAsHex(groupName.getBytes(StandardCharsets.UTF_8));
        // TODO удалить, когда поле lastModified будет заполнено у всех групп в БД
        lastModified = lastModified == null ? ZonedDateTime.now() : lastModified;
        try {
            return restClient
                    .get()
                    .uri("{groupNameMd5}.json", groupNameMd5)
                    .ifModifiedSince(lastModified)
                    .exchange((request, response) -> {
                        if (response.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {
                            return new MaiGroupLessons.NotModified();
                        } else {
                            ZonedDateTime nlm = response.getHeaders().getFirstZonedDateTime(HttpHeaders.LAST_MODIFIED);
                            return response.bodyTo(MaiGroupLessons.Modified.class).setLastModified(nlm);
                        }
                    });
        } catch (JsonParseException | RestClientException e) {
            return new MaiGroupLessons.Failed(groupName, e);
        }
    }
}
