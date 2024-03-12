package ru.maipomogator.bot.processors.message;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

@Component
public class DefaultMessageProcessor implements MessageProcessor {

    @Override
    public List<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId) {
        SendMessage message = new SendMessage(chatId,
                "На всякий случай проверьте ввод. Если уверены, что всё верно, то вероятно данный тип сообщения пока не поддерживается. Следите за обновлениями в @maipomogator_news");
        return List.of(message);
    }

    @Override
    public boolean applies(String text) {
        throw new UnsupportedOperationException(
                "check() in DefaultMessageProcessor must not be invoked, as it is fallback class.");
    }

    @Override
    public String getRegex() {
        throw new UnsupportedOperationException(
                "getRegex() in DefaultMessageProcessor must not be invoked, as it is fallback class.");
    }
}
