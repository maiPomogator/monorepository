package ru.maipomogator.bot.processors.message;

import java.util.Collection;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

import ru.maipomogator.bot.processors.AbstractUpdateProcessor;

public abstract class AbstractMessageProcessor extends AbstractUpdateProcessor<Message> implements MessageProcessor {

    protected AbstractMessageProcessor(String regex) {
        super(regex);
    }

    public Collection<BaseRequest<?, ? extends BaseResponse>> process(Message msg) {
        return process(msg, msg.chat().id());
    }

    @Override
    public boolean applies(Message message) {
        return applies(message.text());
    }

    protected abstract Collection<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId);
}
