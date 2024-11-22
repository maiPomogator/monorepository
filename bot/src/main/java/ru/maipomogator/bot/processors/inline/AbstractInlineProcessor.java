package ru.maipomogator.bot.processors.inline;

import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import ru.maipomogator.bot.processors.AbstractUpdateProcessor;

public abstract class AbstractInlineProcessor extends AbstractUpdateProcessor<InlineQuery> implements InlineProcessor {
    protected AbstractInlineProcessor(String regex) {
        super(regex);
    }

    @Override
    public boolean applies(InlineQuery inlineQuery) {
        return applies(inlineQuery.getQuery());
    }
}
