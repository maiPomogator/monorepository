package ru.maipomogator.bot.processors.inline;

import java.util.Collection;

import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

public interface InlineQueryProcessor {

    Collection<? extends BaseRequest<?, ? extends BaseResponse>> process(InlineQuery query);

    String getRegex();

    default boolean applies(String text) {
        return text.matches(getRegex());
    }
}
