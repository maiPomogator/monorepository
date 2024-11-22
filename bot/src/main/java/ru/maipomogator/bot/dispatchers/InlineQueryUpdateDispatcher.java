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
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.bot.processors.inline.InlineProcessor;

@Log4j2
@Component
public class InlineQueryUpdateDispatcher extends AbstractUpdateDispatcher<InlineQuery> {

    private final List<InlineProcessor> inlineProcessors;

    public InlineQueryUpdateDispatcher(/* TelegramBot bot, */ List<InlineProcessor> processors,
            @Qualifier("default") InlineProcessor defaultProcessor) {
        super(/* bot, */ defaultProcessor);
        processors.remove(defaultProcessor);
        this.inlineProcessors = processors.stream().filter(p -> !p.equals(defaultProcessor)).toList();
    }

    protected Collection<? extends BotApiMethod<? extends Serializable>> processUpdate(Update update) {
        if (!update.hasInlineQuery()) {
            return List.of();
        }
        InlineQuery query = update.getInlineQuery();
        User user = query.getFrom();
        log.info("Processing inline update from {} ({})", user.getUserName(), user.getId());
        List<BotApiMethod<? extends Serializable>> requests = new ArrayList<>();
        boolean isProcessed = false;
        for (InlineProcessor processor : inlineProcessors) {
            if (processor.applies(query)) {
                requests.addAll(processor.process(query));
                isProcessed = true;
                break;
            }
        }
        if (!isProcessed)
            requests.addAll(defaultProcessor.process(query));

        return requests;
    }
}
