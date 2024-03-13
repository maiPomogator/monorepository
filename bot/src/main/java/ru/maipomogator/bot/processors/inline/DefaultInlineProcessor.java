package ru.maipomogator.bot.processors.inline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

@Component
public class DefaultInlineProcessor implements InlineQueryProcessor {

    @Override
    public Collection<? extends BaseRequest<?, ? extends BaseResponse>> process(InlineQuery query) {
        // InlineQueryResultArticle result = new InlineQueryResultArticle("default", "Заголовок
        // непонятного",
        // "Текст непонятного").url("t.me/maipomogator_chat").hideUrl(true);
        // return List.of(new AnswerInlineQuery(query.id(), result).cacheTime(10));

        return List.of();
    }

    @Override
    public String getRegex() {
        throw new UnsupportedOperationException(
                "getRegex() in DefaultInlineProcessor must not be invoked, as it is fallback class.");
    }
}
