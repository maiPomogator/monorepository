package ru.maipomogator.bot.processors.inline;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

@Component
public class DefaultInlineProcessor extends AbstractInlineProcessor {

    protected DefaultInlineProcessor() {
        super("");
    }

    @Override
    public Collection<? extends BaseRequest<?, ? extends BaseResponse>> process(InlineQuery query) {
        // InlineQueryResultArticle result = new InlineQueryResultArticle("default", "Заголовок
        // непонятного",
        // "Текст непонятного").url("t.me/maipomogator_chat").hideUrl(true);
        // return List.of(new AnswerInlineQuery(query.id(), result).cacheTime(10));

    @Override
    public boolean applies(String text) {
        throw new UnsupportedOperationException(
                "applies() in DefaultInlineProcessor must not be invoked, as it is fallback class.");
    }
}
