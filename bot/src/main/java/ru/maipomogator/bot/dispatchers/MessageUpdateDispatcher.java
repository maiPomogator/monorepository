package ru.maipomogator.bot.dispatchers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.bot.processors.message.MessageProcessor;

@Log4j2
@Component
public class MessageUpdateDispatcher extends AbstractUpdateDispatcher<Message> {

    private final List<MessageProcessor> messageProcessors;

    public MessageUpdateDispatcher(/* TelegramBot bot, */ List<MessageProcessor> processors,
            @Qualifier("default") MessageProcessor defaultProcessor) {
        super(/* bot, */ defaultProcessor);
        processors.remove(defaultProcessor);
        this.messageProcessors = processors.stream().filter(p -> !p.equals(defaultProcessor)).toList();
    }

    protected Collection<? extends BotApiMethod<? extends Serializable>> processUpdate(Update update) {
        if (update.getMessage() == null) {
            return List.of();
        }
        Message message = update.getMessage();
        User user = message.getFrom();
        log.info("Processing message update from {} ({})", user.getUserName(), user.getId());
        List<BotApiMethod<? extends Serializable>> requests = new ArrayList<>();
        boolean isProcessed = false;
        for (MessageProcessor processor : messageProcessors) {
            if (processor.applies(message)) {
                requests.addAll(processor.process(message));
                isProcessed = true;
                break;
            }
        }
        if (!isProcessed) {
            requests.addAll(defaultProcessor.process(message));
        }

        return requests;
    }
}
