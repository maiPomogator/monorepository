package ru.maipomogator.bot.processors.inline;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

@Component
@Qualifier("default")
public class DefaultInlineProcessor extends AbstractInlineProcessor {

    protected DefaultInlineProcessor() {
        super("");
    }

    @Override
    public Collection<BotApiMethod<? extends Serializable>> process(InlineQuery query) {
        InlineQueryResultArticle example = InlineQueryResultArticle.builder()
                .id("default")
                .title("Пример inline сообщения")
                .description("Нажмите, чтобы попробовать")
                .inputMessageContent(new InputTextMessageContent("Так будет выглядеть сообщение с расписанием"))
                .build();

        return List.of(AnswerInlineQuery.builder()
                .inlineQueryId(query.getId())
                .result(example)
                .build());
    }

    @Override
    public boolean applies(String text) {
        throw new UnsupportedOperationException(
                "applies() in DefaultInlineProcessor must not be invoked, as it is fallback class.");
    }
}
