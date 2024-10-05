package ru.maipomogator.bot.dispatchers;

import java.util.Collection;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.bot.processors.UpdateProcessor;

@RequiredArgsConstructor
public abstract class AbstractUpdateDispatcher<T> implements UpdateDispatcher {
    private final TelegramBot bot;
    protected final UpdateProcessor<? super T> defaultProcessor;

    @Override
    public void dispatch(Update update) {
        Collection<? extends BaseRequest<?, ? extends BaseResponse>> requests = processUpdate(update);
        for (BaseRequest<?, ? extends BaseResponse> baseRequest : requests) {
            bot.execute(baseRequest);
        }
    }

    protected abstract Collection<? extends BaseRequest<?, ? extends BaseResponse>> processUpdate(Update update);
}
