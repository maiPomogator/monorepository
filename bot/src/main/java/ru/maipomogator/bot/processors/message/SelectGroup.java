package ru.maipomogator.bot.processors.message;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

import ru.maipomogator.bot.clients.GroupRestClient;
import ru.maipomogator.bot.model.Group;
import ru.maipomogator.bot.processors.callback.CancelCallbackProcessor;

@Component
public class SelectGroup extends AbstractMessageProcessor {

    private final GroupRestClient groupRestClient;

    protected SelectGroup(GroupRestClient groupRestClient) {
        super("^(?:[МмТт][\\dИиСсУу]{1,2}[ОоВвЗз]-)?\\d{3}(?:[БбМмАаСс][КкВв]?[КкИи]?)?(?:-\\d{2}$)?$");
        this.groupRestClient = groupRestClient;
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId) {
        String request = msg.text();
        List<Group> groups = groupRestClient.findByName(request);
        SendMessage response;
        if (groups == null || groups.isEmpty()) {
            response = new SendMessage(chatId,
                    "По запросу \"%s\" группа не найдена. На всякий случай проверьте ввод. Если вы уверены, что всё правильно, напишите в @maipomogator_chat"
                            .formatted(request))
                                    .replyMarkup(new InlineKeyboardMarkup(CancelCallbackProcessor.cancelButton()));
        } else {
            InlineKeyboardMarkup keyboard = getInlineKeyboard(groups);
            response = new SendMessage(chatId, "Результаты поиска по запросу \"%s\":".formatted(request))
                    .replyMarkup(keyboard);
        }
        return List.of(new DeleteMessage(chatId, msg.messageId()), response);
    }

    private InlineKeyboardMarkup getInlineKeyboard(List<Group> groups) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        for (Group group : groups) {
            keyboard.addRow(new InlineKeyboardButton(group.name()).callbackData("grp=" + group.id()));
        }
        keyboard.addRow(CancelCallbackProcessor.cancelButton());
        return keyboard;
    }
}
