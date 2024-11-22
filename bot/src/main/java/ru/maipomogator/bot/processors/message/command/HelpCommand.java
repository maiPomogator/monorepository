package ru.maipomogator.bot.processors.message.command;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
public class HelpCommand extends AbstractCommandProcessor {

    protected HelpCommand() {
        super("help", false, "Помощь по работе с ботом");
    }

    @Override
    protected Collection<BotApiMethod<? extends Serializable>> process(Message msg, String chatId) {
        SendMessage helpMessage = new SendMessage(chatId,
                "Постепенно здесь будут появляться ответы на часто задаваемые вопросы.\n\nТакже с вопросами можно обращаться в @maipomogator_chat");
        return List.of(helpMessage);
    }
}
