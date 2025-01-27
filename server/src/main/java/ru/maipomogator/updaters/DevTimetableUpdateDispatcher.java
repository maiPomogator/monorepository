package ru.maipomogator.updaters;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile("development")
@Component
public class DevTimetableUpdateDispatcher {

    // TODO придумать, как запускать не все одновременно
    private final Collection<TimetableUpdater> updaters;

    @Scheduled(initialDelay = 1, timeUnit = TimeUnit.SECONDS)
    @Transactional
    public void updateDev() {
        updaters.forEach(TimetableUpdater::update);
    }
}
