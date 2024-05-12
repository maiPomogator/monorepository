package ru.maipomogator.bot.util;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@Component
public class ButtonFactory {
    private final String cancelCallback;

    public InlineKeyboardButton cancelButton() {
        return new InlineKeyboardButton("❌"). callbackData(cancelCallback);
    }

    public InlineKeyboardButton backButton(String prefix) {
        return new InlineKeyboardButton("↩️").callbackData(prefix + ";back");
    }
}
