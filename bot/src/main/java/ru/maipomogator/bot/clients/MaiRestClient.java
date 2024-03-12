package ru.maipomogator.bot.clients;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.bot.model.MaiInfo;

@Component
@RequiredArgsConstructor
public class MaiRestClient {

    private final RestClient restClient;

    public MaiInfo getMaiInfo() {
        return this.restClient.get().uri("/mai").retrieve().body(MaiInfo.class);
    }
}
