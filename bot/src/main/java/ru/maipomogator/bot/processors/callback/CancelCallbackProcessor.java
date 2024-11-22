package ru.maipomogator.bot.processors.callback;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CancelCallbackProcessor extends AbstractCallbackProcessor {

    protected CancelCallbackProcessor(String cancelCallback) {
        super("^" + cancelCallback + "$");
    }

    @Override
    protected Collection<BotApiMethod<? extends Serializable>> process(CallbackQuery callback, Integer msgId,
            Long chatId) {
        return List.of(answer(callback.id()), new DeleteMessage(chatId, msgId));
    }

    @Override
    protected Collection<BotApiMethod<? extends Serializable>> processInline(CallbackQuery callback,
            String inlineMessageId) {
        return List.of(answer(callback.id()).text("Удалить данное сообщение невозможно."));
    }
}
