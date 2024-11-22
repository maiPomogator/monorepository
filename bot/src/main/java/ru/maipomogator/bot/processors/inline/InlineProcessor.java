package ru.maipomogator.bot.processors.inline;

import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import ru.maipomogator.bot.processors.UpdateProcessor;

public interface InlineProcessor extends UpdateProcessor<InlineQuery> {}
