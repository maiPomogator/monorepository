package ru.maipomogator.bot.processors;

import java.util.Collection;

import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

public interface UpdateProcessor<T> {
    boolean applies(String text);

    Collection<? extends BaseRequest<?, ? extends BaseResponse>> process(T query);
}
