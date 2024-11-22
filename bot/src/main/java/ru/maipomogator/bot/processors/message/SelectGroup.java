package ru.maipomogator.bot.processors.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import ru.maipomogator.bot.clients.GroupRestClient;
import ru.maipomogator.bot.model.Group;

@Component
public class SelectGroup extends AbstractMessageProcessor {

    private final GroupRestClient groupRestClient;

    protected SelectGroup(GroupRestClient groupRestClient) {
        super("^(?:[МмТт][\\dИиСсУу]{1,2}[ОоВвЗз]-)?\\d{3}(?:[БбМмАаСс][КкВв]?[КкИи]?)?(?:-\\d{2}$)?$");
        this.groupRestClient = groupRestClient;
    }

    @Override
    protected Collection<BotApiMethod<? extends Serializable>> process(Message msg, String chatId) {
        String request = msg.getText();
        List<Group> groups = groupRestClient.findByName(request);
        SendMessage response;
        if (groups == null || groups.isEmpty()) {
            response = SendMessage.builder()
                    .chatId(chatId)
                    .text("По запросу \"%s\" группа не найдена. На всякий случай проверьте ввод. Если вы уверены, что всё правильно, напишите в @maipomogator_chat"
                            .formatted(request))
                    .replyMarkup(
                            InlineKeyboardMarkup.builder()
                                    .keyboardRow(new InlineKeyboardRow(buttons.cancelButton()))
                                    .build())
                    .build();
        } else {
            InlineKeyboardMarkup keyboard = getInlineKeyboard(groups);
            response = new SendMessage(chatId, "Результаты поиска по запросу \"%s\":".formatted(request));
            response.setReplyMarkup(keyboard);
        }
        return List.of(new DeleteMessage(chatId, msg.getMessageId()), response);
    }

    private InlineKeyboardMarkup getInlineKeyboard(List<Group> groups) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>(groups.size() + 1);
        for (Group group : groups) {
            InlineKeyboardButton button = new InlineKeyboardButton(group.name());
            button.setCallbackData("grp=" + group.id());
            keyboard.add(new InlineKeyboardRow(button));
        }
        // keyboard.addRow(buttons.cancelButton()); // TODO вернуть кнопку отмены

        return new InlineKeyboardMarkup(keyboard);
    }
}
