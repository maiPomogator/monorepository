package ru.maipomogator.bot.processors.message;

import java.io.Serializable;
import java.util.Collection;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import ru.maipomogator.bot.processors.AbstractUpdateProcessor;

public abstract class AbstractMessageProcessor extends AbstractUpdateProcessor<Message> implements MessageProcessor {

    protected AbstractMessageProcessor(String regex) {
        super(regex);
    }

    public Collection<BotApiMethod<? extends Serializable>> process(Message msg) {
        return process(msg, msg.getChat().getId().toString());
    }

    @Override
    public boolean applies(Message message) {
        return applies(message.getText());
    }

    protected abstract Collection<BotApiMethod<? extends Serializable>> process(Message msg, String chatId);
}
