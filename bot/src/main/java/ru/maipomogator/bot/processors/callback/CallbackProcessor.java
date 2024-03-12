package ru.maipomogator.bot.processors.callback;

import java.util.List;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

public interface CallbackProcessor {

    // метод, указанный для использования вместо Deprecated message() является
    // private в текущей версии 🤷‍♂️
    @SuppressWarnings("deprecation")
    default List<BaseRequest<?, ? extends BaseResponse>> process(CallbackQuery callback) {
        return process(callback, callback.message().messageId(), callback.message().chat().id());
    }

    List<BaseRequest<?, ? extends BaseResponse>> process(CallbackQuery callback, Integer messageId, Long chatId);

    String getRegex();

    default boolean applies(String text) {
        return text.matches(getRegex());
    }

    default AnswerCallbackQuery answer(String callbackId) {
        return new AnswerCallbackQuery(callbackId);
    }
}
