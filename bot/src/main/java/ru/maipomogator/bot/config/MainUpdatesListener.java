package ru.maipomogator.bot.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.bot.processors.callback.CallbackProcessor;
import ru.maipomogator.bot.processors.callback.DefaultCallbackProcessor;
import ru.maipomogator.bot.processors.message.DefaultMessageProcessor;
import ru.maipomogator.bot.processors.message.MessageProcessor;

@Log4j2
@Component
public class MainUpdatesListener implements UpdatesListener {

    private final TelegramBot bot;

    private final List<MessageProcessor> messageProcessors;
    private final DefaultMessageProcessor dmp;
    private final List<CallbackProcessor> callbackProcessors;
    private final DefaultCallbackProcessor dcp;

    public MainUpdatesListener(TelegramBot bot,
            List<MessageProcessor> messageProcessors, DefaultMessageProcessor dmp,
            List<CallbackProcessor> callbackProcessors, DefaultCallbackProcessor dcp) {
        this.bot = bot;

        messageProcessors.remove(dmp);
        this.messageProcessors = messageProcessors;
        this.dmp = dmp;

        callbackProcessors.remove(dcp);
        this.callbackProcessors = callbackProcessors;
        this.dcp = dcp;
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {

            if (hasTextMessage(update)) {
                Message message = update.message();
                log.info("Processing message update from {} ({})", message.from().username(), message.from().id());
                processMessage(message).forEach(bot::execute);
            } else if (hasCallbackQuery(update)) {
                CallbackQuery callback = update.callbackQuery();
                log.info("Processing callback update from {} ({})", callback.from().username(), callback.from().id());
                processCallbackQuery(callback).forEach(bot::execute);
            } else {
                log.info("Received update is not supported. Skipping.");
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private List<BaseRequest<?, ? extends BaseResponse>> processMessage(Message message) {
        List<BaseRequest<?, ? extends BaseResponse>> requests = new ArrayList<>();
        boolean isProcessed = false;
        for (MessageProcessor processor : messageProcessors) {
            if (processor.applies(message.text())) {
                requests.addAll(processor.process(message));
                isProcessed = true;
                break;
            }
        }
        if (!isProcessed)
            requests.addAll(dmp.process(message));

        return requests;
    }

    private List<BaseRequest<?, ? extends BaseResponse>> processCallbackQuery(CallbackQuery callback) {
        List<BaseRequest<?, ? extends BaseResponse>> requests = new ArrayList<>();
        boolean isProcessed = false;
        for (CallbackProcessor processor : callbackProcessors) {
            if (processor.applies(callback.data())) {
                requests.addAll(processor.process(callback));
                isProcessed = true;
                break;
            }
        }
        if (!isProcessed)
            requests.addAll(dcp.process(callback));

        return requests;
    }

    private boolean hasTextMessage(Update update) {
        return update.message() != null && update.message().text() != null;
    }

    private boolean hasCallbackQuery(Update update) {
        return update.callbackQuery() != null;
    }
}
