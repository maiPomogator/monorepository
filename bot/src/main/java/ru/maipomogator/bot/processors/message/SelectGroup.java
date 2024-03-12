package ru.maipomogator.bot.processors.message;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.bot.clients.GroupRestClient;
import ru.maipomogator.bot.model.Group;

@Component
@RequiredArgsConstructor
public class SelectGroup implements MessageProcessor {

    private final GroupRestClient groupsRestClient;

    @Override
    public List<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId) {
        Group group = groupsRestClient.findByName(msg.text());
        if (group == null) {
            return List.of(new SendMessage(chatId, "Группа не найдена. Проверьте название. Если вы уверены, что оно правильное, напишите в @maipomogator_chat"));
        }

        return List.of(new SendMessage(chatId, "Выберите свою группу").replyMarkup(getInlineKeyboard(group)));
    }

    private InlineKeyboardMarkup getInlineKeyboard(Group group) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton groupBtn = new InlineKeyboardButton(group.name()).callbackData("grp=" + group.id());
        return keyboard.addRow(groupBtn);
    }

    @Override
    public String getRegex() {
        // регулярка строгая, не позволяет отклонений от правильного написания
        return "^[МмТт][\\dИиУу]{1,2}[ОоВвЗз]-\\d{3}[БбМмАаСс][КкВв]?[КкИи]?-\\d{2}$";
    }
}
