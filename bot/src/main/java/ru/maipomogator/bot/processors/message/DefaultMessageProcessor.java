package ru.maipomogator.bot.processors.message;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
@Qualifier("default")
public class DefaultMessageProcessor extends AbstractMessageProcessor {

    protected DefaultMessageProcessor() {
        super("");
    }

    @Override
    protected Collection<BotApiMethod<? extends Serializable>> process(Message msg, String chatId) {
        SendMessage message = new SendMessage(chatId,
                "На всякий случай проверьте ввод. "
                        + "Если уверены, что всё верно, то вероятно такой формат ввода пока не поддерживается. "
                        + "Следите за обновлениями в @maipomogator или напишите нам в @maipomogator_chat");
        return List.of(message);
    }

    @Override
    public boolean applies(String text) {
        throw new UnsupportedOperationException(
                "applies() in DefaultMessageProcessor must not be invoked, as it is fallback class.");
    }
}
