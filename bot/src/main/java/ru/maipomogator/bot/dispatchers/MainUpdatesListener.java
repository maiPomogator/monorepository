package ru.maipomogator.bot.dispatchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

@Component
public class MainUpdatesListener implements UpdatesListener {

    private List<UpdateDispatcher> dispatchers;
    private UpdateDispatcher defaultDispatcher;

    public MainUpdatesListener(List<UpdateDispatcher> dispatchers,
            @Qualifier("default") UpdateDispatcher defaultDispatcher) {
        this.dispatchers = dispatchers.stream().filter(d -> !d.equals(defaultDispatcher)).toList();
        this.defaultDispatcher = defaultDispatcher;
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            dispatchers.forEach(d -> d.dispatch(update));
        }
        //TODO Добавить fallback обработку в defaultDispatcher
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
