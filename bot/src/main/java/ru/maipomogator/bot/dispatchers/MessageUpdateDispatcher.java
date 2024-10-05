package ru.maipomogator.bot.dispatchers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.bot.processors.message.MessageProcessor;

@Log4j2
@Component
public class MessageUpdateDispatcher extends AbstractUpdateDispatcher<Message> {

    private final List<MessageProcessor> messageProcessors;

    public MessageUpdateDispatcher(TelegramBot bot, List<MessageProcessor> processors,
            @Qualifier("default") MessageProcessor defaultProcessor) {
        super(bot, defaultProcessor);
        processors.remove(defaultProcessor);
        this.messageProcessors = processors.stream().filter(p -> !p.equals(defaultProcessor)).toList();
    }

    protected Collection<? extends BaseRequest<?, ? extends BaseResponse>> processUpdate(Update update) {
        if (update.message() == null) {
            return List.of();
        }
        Message message = update.message();
        User user = message.from();
        log.info("Processing message update from {} ({})", user.username(), user.id());
        List<BaseRequest<?, ? extends BaseResponse>> requests = new ArrayList<>();
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
