package ru.maipomogator.bot.processors.message.command;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.bot.processors.callback.ChoosingGroupProcessor;

@Component
@RequiredArgsConstructor
public class NewGroupCommand implements CommandProcessor {

    private final ChoosingGroupProcessor cgp;

    @Override
    public List<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, msg.messageId());
        SendMessage message = cgp.getInstitutesMessage(chatId);
        return List.of(deleteMessage, message);
    }

    @Override
    public String getCommand() {
        return "newgroup";
    }

    @Override
    public String getDescription() {
        return "Интерактивный выбор группы";
    }
}
