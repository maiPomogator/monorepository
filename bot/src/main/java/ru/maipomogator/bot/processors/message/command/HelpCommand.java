package ru.maipomogator.bot.processors.message.command;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

@Component
public class HelpCommand extends AbstractCommandProcessor {

    protected HelpCommand() {
        super("help", false, "Помощь по работе с ботом");
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId) {
        SendMessage helpMessage = new SendMessage(chatId,
                "Постепенно здесь будут появляться ответы на часто задаваемые вопросы.\n\nТакже с вопросами можно обращаться в @maipomogator_chat");
        return List.of(helpMessage);
    }
}
