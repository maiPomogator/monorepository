package ru.maipomogator.bot.processors.message.command;

import ru.maipomogator.bot.processors.message.MessageProcessor;

public interface CommandProcessor extends MessageProcessor {
    /**
     * @return команда, которая будет обработана, без ведущего /
     */
    String getCommand();

    /**
     * @return описание команды
     */
    String getDescription();
}
