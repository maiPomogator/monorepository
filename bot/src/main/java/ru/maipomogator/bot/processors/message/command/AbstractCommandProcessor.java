package ru.maipomogator.bot.processors.message.command;

import ru.maipomogator.bot.processors.message.AbstractMessageProcessor;

public abstract class AbstractCommandProcessor extends AbstractMessageProcessor implements CommandProcessor {
    private final String command;
    private final String description;

    protected AbstractCommandProcessor(String command, String description) {
        super("^/" + command + ".*$");
        this.command = command;
        this.description = description;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
