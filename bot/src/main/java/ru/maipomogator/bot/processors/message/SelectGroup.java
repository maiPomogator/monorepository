package ru.maipomogator.bot.processors.message;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

import ru.maipomogator.bot.clients.GroupRestClient;
import ru.maipomogator.bot.model.Group;

@Component
public class SelectGroup extends AbstractMessageProcessor {

    private final GroupRestClient groupsRestClient;

    protected SelectGroup(GroupRestClient groupsRestClient) {
        // регулярка строгая, не позволяет отклонений от правильного написания
        super("^[МмТт][\\dИиУу]{1,2}[ОоВвЗз]-\\d{3}[БбМмАаСс][КкВв]?[КкИи]?-\\d{2}$");
        this.groupsRestClient = groupsRestClient;
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId) {
        Group group = groupsRestClient.findByName(msg.text());
        if (group == null) {
            return List.of(new SendMessage(chatId,
                    "Группа не найдена. Проверьте название. Если вы уверены, что оно правильное, напишите в @maipomogator_chat"));
        }

        return List.of(new SendMessage(chatId, "Выберите свою группу").replyMarkup(getInlineKeyboard(group)));
    }

    private InlineKeyboardMarkup getInlineKeyboard(Group group) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton groupBtn = new InlineKeyboardButton(group.name()).callbackData("grp=" + group.id());
        return keyboard.addRow(groupBtn);
    }
}
