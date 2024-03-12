package ru.maipomogator.bot.processors.callback;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.response.BaseResponse;

@Component
public class CancelCallbackProcessor implements CallbackProcessor {

    private static final String CANCEL_CALLBACK = "cancel";

    public static InlineKeyboardButton cancelButton() {
        return new InlineKeyboardButton("❌").callbackData(CANCEL_CALLBACK);
    }

    @Override
    public List<BaseRequest<?, ? extends BaseResponse>> process(CallbackQuery callback, Integer msgId, Long chatId) {
        return List.of(answer(callback.id()), new DeleteMessage(chatId, msgId));
    }

    @Override
    public String getRegex() {
        return "^" + CANCEL_CALLBACK + "$";
    }
}
