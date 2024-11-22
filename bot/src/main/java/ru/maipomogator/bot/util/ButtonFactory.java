package ru.maipomogator.bot.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@Component
public class ButtonFactory {
    private final String cancelCallback;

    public InlineKeyboardButton cancelButton() {
        return InlineKeyboardButton.builder().text("❌").callbackData(cancelCallback).build();
    }

    public InlineKeyboardButton backButton(String prefix) {
        return InlineKeyboardButton.builder().text("↩️").callbackData(prefix + ";back").build();
    }
}
