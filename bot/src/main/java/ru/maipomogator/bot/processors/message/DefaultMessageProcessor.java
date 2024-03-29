package ru.maipomogator.bot.processors.message;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

@Component
@Qualifier("default")
public class DefaultMessageProcessor extends AbstractMessageProcessor {

    protected DefaultMessageProcessor() {
        super("");
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId) {
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
