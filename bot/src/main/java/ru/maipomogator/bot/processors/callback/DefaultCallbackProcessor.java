package ru.maipomogator.bot.processors.callback;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@Qualifier("default")
public class DefaultCallbackProcessor extends AbstractCallbackProcessor {

    private static final String BUTTON_NOT_SUPPORTED = "Данная кнопка пока не поддерживается.";

    protected DefaultCallbackProcessor() {
        super("");
    }

    @Override
    protected Collection<BotApiMethod<? extends Serializable>> process(CallbackQuery callback, Integer msgId,
            Long chatId) {
        return List.of(answer(callback.getId()).text(BUTTON_NOT_SUPPORTED));
    }

    @Override
    protected Collection<BotApiMethod<? extends Serializable>> processInline(CallbackQuery callback,
            String inlineMessageId) {
        return List.of(answer(callback.getId()).text(BUTTON_NOT_SUPPORTED));
    }

    @Override
    public boolean applies(String text) {
        throw new UnsupportedOperationException(
                "applies() in DefaultCallbackProcessor must not be invoked, as it is fallback class.");
    }
}
