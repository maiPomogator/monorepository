package ru.maipomogator.bot.processors.message.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
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
        List<BaseRequest<?, ? extends BaseResponse>> list = new ArrayList<>();
        SendMessage message = new SendMessage(chatId, "Приветствуем в нашем боте.\n"
                + "Для выбора группы введите её номер или /newgroup для интерактивного выбора. "
                + "Для выбора преподавателя введите фамилию или имя и отчество, или ФИО целиком.\n\n"
                + "Также имеется поддержка inline-режима, благодаря которому можно отправить сообщение с расписанием в любом чате.")
                        .replyMarkup(getKeyboard());
        list.add(message);
        return list;
    }

    private InlineKeyboardMarkup getKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addRow(new InlineKeyboardButton("Попробовать inline").switchInlineQueryCurrentChat(""));
        keyboard.addRow(new InlineKeyboardButton("Отправить расписание в другой чат").switchInlineQuery(""));
        return keyboard;
    }
}
