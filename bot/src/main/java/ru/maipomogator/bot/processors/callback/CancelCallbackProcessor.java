package ru.maipomogator.bot.processors.callback;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.response.BaseResponse;

@Component
public class CancelCallbackProcessor extends AbstractCallbackProcessor {

    protected CancelCallbackProcessor(String cancelCallback) {
        super("^" + cancelCallback + "$");
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(CallbackQuery callback, Integer msgId,
            Long chatId) {
        return List.of(answer(callback.id()), new DeleteMessage(chatId, msgId));
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> processInline(CallbackQuery callback,
            String inlineMessageId) {
        return List.of(answer(callback.id()).text("Удалить данное сообщение невозможно."));
    }
}
