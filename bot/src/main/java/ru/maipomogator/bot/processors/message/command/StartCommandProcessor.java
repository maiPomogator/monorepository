package ru.maipomogator.bot.processors.message.command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class StartCommandProcessor extends AbstractCommandProcessor {

    protected StartCommandProcessor() {
        super("start", true, "Перезапуск бота");
    }

    @Override
    protected Collection<BotApiMethod<? extends Serializable>> process(Message msg, String chatId) {
        List<BotApiMethod<? extends Serializable>> list = new ArrayList<>();
        SendMessage message = new SendMessage(chatId, "Приветствуем в нашем боте.\n"
                + "За обновлениями можно следить в канале @maipomogator. "
                + "Вопросы и пожелания можно (и нужно) писать в комментарии или @maipomogator_chat.\n\n"
                + "Для выбора группы введите её номер или /newgroup для интерактивного выбора.\n"
                + "Для выбора преподавателя введите фамилию/имя и отчество/ФИО целиком.\n\n"
                + "Также имеется поддержка inline-режима, благодаря которому можно отправить сообщение с расписанием в любом чате.");
        message.setReplyMarkup(getKeyboard());
        list.add(message);
        return list;
    }

    private InlineKeyboardMarkup getKeyboard() {
        List<InlineKeyboardRow> keyboard = new ArrayList<>(3);

        InlineKeyboardButton button = new InlineKeyboardButton("Попробовать inline");
        button.setSwitchInlineQueryCurrentChat("");
        keyboard.add(new InlineKeyboardRow(button));
        InlineKeyboardButton button2 = new InlineKeyboardButton("Отправить расписание в другой чат");
        button2.setSwitchInlineQuery("");
        keyboard.add(new InlineKeyboardRow(button2));
        return new InlineKeyboardMarkup(keyboard);
    }
}
