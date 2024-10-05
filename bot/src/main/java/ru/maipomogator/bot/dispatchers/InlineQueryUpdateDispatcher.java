package ru.maipomogator.bot.dispatchers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.bot.processors.inline.InlineProcessor;

@Log4j2
@Component
public class InlineQueryUpdateDispatcher extends AbstractUpdateDispatcher<InlineQuery> {

    private final List<InlineProcessor> inlineProcessors;

    public InlineQueryUpdateDispatcher(TelegramBot bot, List<InlineProcessor> processors,
            @Qualifier("default") InlineProcessor defaultProcessor) {
        super(bot, defaultProcessor);
        processors.remove(defaultProcessor);
        this.inlineProcessors = processors.stream().filter(p -> !p.equals(defaultProcessor)).toList();
    }

    protected Collection<? extends BaseRequest<?, ? extends BaseResponse>> processUpdate(Update update) {
        if (update.inlineQuery() == null) {
            return List.of();
        }
        InlineQuery query = update.inlineQuery();
        User user = query.from();
        log.info("Processing inline update from {} ({})", user.username(), user.id());
        List<BaseRequest<?, ? extends BaseResponse>> requests = new ArrayList<>();
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
