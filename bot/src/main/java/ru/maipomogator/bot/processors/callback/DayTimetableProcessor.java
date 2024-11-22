package ru.maipomogator.bot.processors.callback;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import ru.maipomogator.bot.timetable.TimetableBuilder;

@Component
public class DayTimetableProcessor extends AbstractCallbackProcessor {
    private final TimetableBuilder builder;

    protected DayTimetableProcessor(TimetableBuilder builder) {
        super("^(?:grp|prf)=\\d{1,4}(?:;date=(?:\\d{6}|today))?$");
        this.builder = builder;
    }

    @Override
    protected Collection<BotApiMethod<? extends Serializable>> process(CallbackQuery callback, Integer msgId,
            Long chatId) {
        String[] segments = callback.data().split(";");
        if (segments.length == 1) {
            InlineKeyboardMarkup keyboard = getControlKeyboard(segments[0], LocalDate.now());
            EditMessageText editMessage = new EditMessageText(chatId, msgId,
                    getPreparedText(segments[0], LocalDate.now()))
                            .replyMarkup(keyboard).parseMode(ParseMode.MarkdownV2);
            return List.of(answer(callback.id()), editMessage);
        } else if (segments.length == 2) {
            LocalDate targetDate = getDateFromCallback(segments[1].split("=")[1]);
            InlineKeyboardMarkup keyboard = getControlKeyboard(segments[0], targetDate);
            EditMessageText editMessage = new EditMessageText(chatId, msgId, getPreparedText(segments[0], targetDate))
                    .replyMarkup(keyboard).parseMode(ParseMode.MarkdownV2);
            return List.of(answer(callback.id()), editMessage);
        }
        return List.of(answer(callback.id()).text(
                "Что-то пошло не так, повторите попытку через некоторое время, либо напишите в @maipomogator_chat"));
    }

    @Override
    protected Collection<BotApiMethod<? extends Serializable>> processInline(CallbackQuery callback,
            String inlineMessageId) {
        String[] segments = callback.data().split(";");
        if (segments.length == 1) {
            InlineKeyboardMarkup keyboard = getControlKeyboard(segments[0], LocalDate.now());
            EditMessageText editMessage = new EditMessageText(inlineMessageId,
                    getPreparedText(segments[0], LocalDate.now()))
                            .replyMarkup(keyboard).parseMode(ParseMode.MarkdownV2);
            return List.of(answer(callback.id()), editMessage);
        } else if (segments.length == 2) {
            LocalDate targetDate = getDateFromCallback(segments[1].split("=")[1]);
            InlineKeyboardMarkup keyboard = getControlKeyboard(segments[0], targetDate);
            EditMessageText editMessage = new EditMessageText(inlineMessageId, getPreparedText(segments[0], targetDate))
                    .replyMarkup(keyboard).parseMode(ParseMode.MarkdownV2);
            return List.of(answer(callback.id()), editMessage);
        }
        return List.of(answer(callback.id()).text(
                "Что-то пошло не так, повторите попытку через некоторое время, либо напишите в @maipomogator_chat"));
    }

    private InlineKeyboardMarkup getControlKeyboard(String prefix, LocalDate curDate) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton back = new InlineKeyboardButton("◀️")
                .callbackData(prefix + ";date=" + curDate.minusDays(1).format(CALLBACK_DATE_FORMATTER));
        InlineKeyboardButton today = new InlineKeyboardButton("Сегодня")
                .callbackData(prefix + ";date=today");
        InlineKeyboardButton fwd = new InlineKeyboardButton("▶️")
                .callbackData(prefix + ";date=" + curDate.plusDays(1).format(CALLBACK_DATE_FORMATTER));
        keyboard.addRow(back, today, fwd);
        keyboard.addRow(new InlineKeyboardButton("По неделям").callbackData(prefix + ";from=current"));
        keyboard.addRow(new InlineKeyboardButton("Экзамены").callbackData(prefix + ";exams"));
        return keyboard;
    }

    private String getPreparedText(String prefix, LocalDate targetDate) {
        String text = builder.getMessageText(prefix, targetDate, targetDate);
        return escapeForMarkdownV2(text);
    }

    private LocalDate getDateFromCallback(String data) {
        return data.equals("today") ? LocalDate.now() : LocalDate.parse(data, CALLBACK_DATE_FORMATTER);
    }
}
