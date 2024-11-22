package ru.maipomogator.bot.processors.message.command;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import ru.maipomogator.bot.processors.callback.ChoosingGroupProcessor;

@Component
public class NewGroupCommand extends AbstractCommandProcessor {

    private final ChoosingGroupProcessor cgp;

    public NewGroupCommand(ChoosingGroupProcessor cgp) {
        super("newgroup", false, "Интерактивный выбор группы");
        this.cgp = cgp;
    }

    @Override
    protected Collection<BotApiMethod<? extends Serializable>> process(Message msg, String chatId) {
        SendMessage message = cgp.getInstitutesMessage(chatId);
        return List.of(new DeleteMessage(chatId, msg.getMessageId()), message);
    }
}
