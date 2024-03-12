package ru.maipomogator.bot.processors.message;

import java.util.List;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

public interface MessageProcessor {
    default List<BaseRequest<?, ? extends BaseResponse>> process(Message msg) {
        return process(msg, msg.chat().id());
    }

    List<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId);

    String getRegex();

    default boolean applies(String text) {
        return text.matches(getRegex());
    }
}
