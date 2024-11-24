package ru.maipomogator.bot.processors.message.command;

import com.pengrad.telegrambot.model.BotCommand;

import lombok.Getter;
import ru.maipomogator.bot.processors.message.AbstractMessageProcessor;

public abstract class AbstractCommandProcessor extends AbstractMessageProcessor implements CommandProcessor {
    @Getter
    private final BotCommand botCommand;

    protected AbstractCommandProcessor(String command, boolean allowArguments, String description) {
        super("^/" + command + (allowArguments ? "(?: .*)?" : "") + "$");
        this.botCommand = new BotCommand(command, description);
    }
}
