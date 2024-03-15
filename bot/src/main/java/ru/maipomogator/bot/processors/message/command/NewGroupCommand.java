package ru.maipomogator.bot.processors.message.command;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

import ru.maipomogator.bot.processors.callback.ChoosingGroupProcessor;

@Component
public class NewGroupCommand extends AbstractCommandProcessor {

    private final ChoosingGroupProcessor cgp;

    public NewGroupCommand(ChoosingGroupProcessor cgp) {
        super("newgroup", "Интерактивный выбор группы");
        this.cgp = cgp;
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, msg.messageId());
        SendMessage message = cgp.getInstitutesMessage(chatId);
        return List.of(deleteMessage, message);
    }
}
