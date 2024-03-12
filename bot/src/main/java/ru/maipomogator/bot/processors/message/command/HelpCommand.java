package ru.maipomogator.bot.processors.message.command;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

@Component
public class HelpCommand implements CommandProcessor {

    @Override
    public List<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId) {
        SendMessage helpMessage = new SendMessage(chatId,
                "В будущем тут будут FAQ и другая полезная информация. А пока подписывайтесь на @maipomogator_news");
        return List.of(helpMessage);
    }

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Помощь по работе с ботом";
    }
}
