package ru.maipomogator.updaters;

import java.util.Collection;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile("production")
@Component
public class ProdTimetableUpdateDispatcher {

    // TODO придумать, как запускать не все одновременно
    private final Collection<TimetableUpdater> updaters;

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void update() {
        updaters.forEach(TimetableUpdater::update);
    }
}
