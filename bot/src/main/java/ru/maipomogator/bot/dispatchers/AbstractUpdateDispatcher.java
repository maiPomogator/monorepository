package ru.maipomogator.bot.dispatchers;

import java.io.Serializable;
import java.util.Collection;
// import java.util.List;

import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.maipomogator.bot.processors.UpdateProcessor;

public abstract class AbstractUpdateDispatcher<T extends BotApiObject> implements UpdateDispatcher {
    // private final TelegramBot bot;
    // protected final List<UpdateProcessor<T>> processors;
    protected final UpdateProcessor<T> defaultProcessor;

    public AbstractUpdateDispatcher(/* TelegramBot bot */ /* , List<UpdateProcessor<T>> processors */ /* , */
            UpdateProcessor<T> defaultProcessor) {
        // this.bot = bot;
        /* this.processors = processors.stream().filter(p -> !p.equals(defaultProcessor)).toList(); */
        this.defaultProcessor = defaultProcessor;
    }

    @Override
    public void dispatch(Update update) {
        Collection<? extends BotApiMethod<? extends Serializable>> requests = processUpdate(update);
        for (BotApiMethod<? extends Serializable> baseRequest : requests) {
            System.out.println(baseRequest.toString());
            // bot.execute(baseRequest);
        }
    }

    protected abstract Collection<? extends BotApiMethod<? extends Serializable>> processUpdate(Update update);
}
