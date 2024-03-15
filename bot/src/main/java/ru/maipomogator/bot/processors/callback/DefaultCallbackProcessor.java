package ru.maipomogator.bot.processors.callback;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

@Component
public class DefaultCallbackProcessor extends AbstractCallbackProcessor {

    protected DefaultCallbackProcessor() {
        super("");
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(CallbackQuery callback, Integer msgId,
            Long chatId) {
        return List.of(answer(callback.id()).text("Данная кнопка пока не поддерживается."));
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> processInline(CallbackQuery callback,
            String inlineMessageId) {
        return List.of(answer(callback.id()).text("Данная кнопка пока не поддерживается."));
    }

    @Override
    public boolean applies(String text) {
        throw new UnsupportedOperationException(
                "applies() in DefaultCallbackProcessor must not be invoked, as it is fallback class.");
    }
}
