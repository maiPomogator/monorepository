package ru.maipomogator.bot.processors.inline;

import com.pengrad.telegrambot.model.InlineQuery;

import ru.maipomogator.bot.processors.AbstractUpdateProcessor;

public abstract class AbstractInlineProcessor extends AbstractUpdateProcessor<InlineQuery> implements InlineProcessor {
    protected AbstractInlineProcessor(String regex) {
        super(regex);
    }

    @Override
    public boolean applies(InlineQuery inlineQuery) {
        return applies(inlineQuery.query());
    }
}
