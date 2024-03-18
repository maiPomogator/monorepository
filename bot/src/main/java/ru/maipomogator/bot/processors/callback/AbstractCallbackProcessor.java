package ru.maipomogator.bot.processors.callback;

import java.util.Collection;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

import ru.maipomogator.bot.processors.AbstractUpdateProcessor;

public abstract class AbstractCallbackProcessor extends AbstractUpdateProcessor<CallbackQuery>
        implements CallbackProcessor {

    protected AbstractCallbackProcessor(String regex) {
        super(regex);
    }

    public Collection<BaseRequest<?, ? extends BaseResponse>> process(CallbackQuery callback) {
        if (isFromInlineMessage(callback)) {
            return processInline(callback, callback.inlineMessageId());
        } else {
            Message message = (Message) callback.maybeInaccessibleMessage();
            return process(callback, message.messageId(), message.chat().id());
        }
    }

    protected abstract Collection<BaseRequest<?, ? extends BaseResponse>> process(
            CallbackQuery callback, Integer msgId, Long chatId);

    protected abstract Collection<BaseRequest<?, ? extends BaseResponse>> processInline(
            CallbackQuery callback, String inlineMessageId);

    protected AnswerCallbackQuery answer(String callbackId) {
        return new AnswerCallbackQuery(callbackId);
    }

    private boolean isFromInlineMessage(CallbackQuery callback) {
        return callback.inlineMessageId() != null;
    }
}
