package ru.maipomogator.bot.dispatchers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.bot.processors.callback.CallbackProcessor;

@Log4j2
@Component
public class CallbackUpdateDispatcher extends AbstractUpdateDispatcher<CallbackQuery> {
    private final List<CallbackProcessor> callbackProcessors;

    public CallbackUpdateDispatcher(TelegramBot bot, List<CallbackProcessor> processors,
            @Qualifier("default") CallbackProcessor defaultProcessor) {
        super(bot, defaultProcessor);
        this.callbackProcessors = processors.stream().filter(p -> !p.equals(defaultProcessor)).toList();
    }

    protected Collection<? extends BaseRequest<?, ? extends BaseResponse>> processUpdate(Update update) {
        if(update.callbackQuery() == null) {
            return List.of();
        }
        CallbackQuery callback = update.callbackQuery();
        User user = callback.from();
        log.info("Processing callback update from {} ({})", user.username(), user.id());
        List<BaseRequest<?, ? extends BaseResponse>> requests = new ArrayList<>();
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
