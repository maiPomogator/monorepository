package ru.maipomogator.bot.processors.message;

import org.telegram.telegrambots.meta.api.objects.message.Message;

import ru.maipomogator.bot.processors.UpdateProcessor;

public interface MessageProcessor extends UpdateProcessor<Message> {}
