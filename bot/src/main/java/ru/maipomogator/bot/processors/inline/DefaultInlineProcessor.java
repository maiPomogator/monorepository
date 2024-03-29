package ru.maipomogator.bot.processors.inline;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

@Component
@Qualifier("default")
public class DefaultInlineProcessor extends AbstractInlineProcessor {

    protected DefaultInlineProcessor() {
        super("");
    }

    @Override
    public Collection<? extends BaseRequest<?, ? extends BaseResponse>> process(InlineQuery query) {
        InlineQueryResultArticle example = new InlineQueryResultArticle("default", "Пример inline сообщения",
                "Так будет выглядеть сообщение с расписанием").description("Нажмите, чтобы попробовать");
        return List.of(new AnswerInlineQuery(query.id(), example));
    }

    @Override
    public boolean applies(String text) {
        throw new UnsupportedOperationException(
                "applies() in DefaultInlineProcessor must not be invoked, as it is fallback class.");
    }
}
