package ru.maipomogator.bot.processors.message.command;

import com.pengrad.telegrambot.model.BotCommand;

import ru.maipomogator.bot.processors.message.MessageProcessor;

public interface CommandProcessor extends MessageProcessor {
    BotCommand getBotCommand();
}
