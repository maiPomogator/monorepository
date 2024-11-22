package ru.maipomogator.bot.dispatchers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.bot.processors.callback.CallbackProcessor;

@Log4j2
@Component
public class CallbackUpdateDispatcher extends AbstractUpdateDispatcher<CallbackQuery> {
    private final List<CallbackProcessor> callbackProcessors;

    public CallbackUpdateDispatcher(/* TelegramBot bot, */ List<CallbackProcessor> processors,
            @Qualifier("default") CallbackProcessor defaultProcessor) {
        super(/* bot, */ defaultProcessor);
        this.callbackProcessors = processors.stream().filter(p -> !p.equals(defaultProcessor)).toList();
    }

    protected Collection<? extends BotApiMethod<? extends Serializable>> processUpdate(Update update) {
        if (!update.hasCallbackQuery()) {
            return List.of();
        }
        CallbackQuery callback = update.getCallbackQuery();
        User user = callback.getFrom();
        log.info("Processing callback update from {} ({})", user.getUserName(), user.getId());
        List<BotApiMethod<? extends Serializable>> requests = new ArrayList<>();
        boolean isProcessed = false;
        for (CallbackProcessor processor : callbackProcessors) {
            if (processor.applies(callback)) {
                requests.addAll(processor.process(callback));
                isProcessed = true;
                break;
            }
        }
        if (!isProcessed)
            requests.addAll(defaultProcessor.process(callback));

        return requests;
    }
}
