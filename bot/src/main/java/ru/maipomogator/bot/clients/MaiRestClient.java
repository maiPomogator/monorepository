package ru.maipomogator.bot.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import ru.maipomogator.bot.model.MaiInfo;

@Component
public class MaiRestClient {

    private final RestClient restClient;

    public MaiRestClient(@Value("${baseurl:https://rufus20145.ru}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public MaiInfo getMaiInfo() {
        return this.restClient.get().uri("/mai").retrieve().body(MaiInfo.class);
    }

}
