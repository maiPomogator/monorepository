package ru.maipomogator.bot.processors.callback;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import ru.maipomogator.bot.processors.AbstractUpdateProcessor;

public abstract class AbstractCallbackProcessor extends AbstractUpdateProcessor<CallbackQuery>
        implements CallbackProcessor {

    protected static final DateTimeFormatter CALLBACK_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    protected AbstractCallbackProcessor(String regex) {
        super(regex);
    }

    public Collection<BotApiMethod<? extends Serializable>> process(CallbackQuery callback) {
        if (isFromInlineMessage(callback)) {
            return processInline(callback, callback.getInlineMessageId());
        } else {
            Message message = (Message) callback.getMessage();
            return process(callback, message.getMessageId(), message.getChat().getId());
        }
    }

    @Override
    public boolean applies(CallbackQuery callbackQuery) {
        return applies(callbackQuery.getData());
    }

    protected abstract Collection<BotApiMethod<? extends Serializable>> process(
            CallbackQuery callback, Integer msgId, Long chatId);

    protected abstract Collection<BotApiMethod<? extends Serializable>> processInline(
            CallbackQuery callback, String inlineMessageId);

    protected AnswerCallbackQuery answer(String callbackId) {
        return new AnswerCallbackQuery(callbackId);
    }

    private boolean isFromInlineMessage(CallbackQuery callback) {
        return callback.getInlineMessageId() != null;
    }
}
