package ru.maipomogator.bot.processors.callback;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import ru.maipomogator.bot.processors.UpdateProcessor;

public interface CallbackProcessor extends UpdateProcessor<CallbackQuery> {}
