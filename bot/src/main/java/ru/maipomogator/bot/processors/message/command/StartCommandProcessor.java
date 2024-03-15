package ru.maipomogator.bot.processors.message.command;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

@Component
public class StartCommandProcessor extends AbstractCommandProcessor {

    protected StartCommandProcessor() {
        super("start", "Перезапуск бота");
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId) {
        SendMessage message = new SendMessage(chatId, "Приветствуем в нашем боте.\n"
                + "Для выбора группы введите её номер или /newgroup для интерактивного выбора."
                + " Для выбора преподавателя введите фамилию или ФИО целиком.")
                        .replyMarkup(new ReplyKeyboardRemove());
        return List.of(message);
    }
}