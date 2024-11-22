package ru.maipomogator.bot.dispatchers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Qualifier("default")
@Component
public class DefaultUpdateDispatcher implements UpdateDispatcher {

    @Override
    public void dispatch(Update update) {
        log.info("Received update with unsupported type");
        log.debug(update.toString());
    }
}
