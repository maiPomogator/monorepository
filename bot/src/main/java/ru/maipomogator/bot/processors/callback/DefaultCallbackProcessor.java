package ru.maipomogator.bot.processors.callback;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

@Component
public class DefaultCallbackProcessor implements CallbackProcessor {

    @Override
    public List<BaseRequest<?, ? extends BaseResponse>> process(CallbackQuery callback, Integer msgId, Long chatId) {
        return List.of(answer(callback.id()).text("Данная кнопка пока не поддерживается."));
    }

    @Override
    public String getRegex() {
        throw new UnsupportedOperationException(
                "getRegex() in DefaultCallbackProcessor must not be invoked, as it is fallback class.");
    }

}
