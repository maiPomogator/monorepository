package ru.maipomogator.bot.processors.callback;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.BaseResponse;

import ru.maipomogator.bot.timetable.TimetableBuilder;

@Component
public class WeekTimetableProcessor extends AbstractCallbackProcessor {
    private final TimetableBuilder builder;

    protected WeekTimetableProcessor(TimetableBuilder builder) {
        super("^(?:grp|prf)=\\d{1,4}(?:;from=(?:\\d{6}|current))?$");
        this.builder = builder;
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(CallbackQuery callback, Integer msgId,
            Long chatId) {
        String[] segments = callback.data().split(";");
        if (segments.length == 1) {
            LocalDate startDate = getDateFromCallback("current");
            LocalDate endDate = startDate.plusDays(7);
            InlineKeyboardMarkup keyboard = getControlKeyboard(segments[0], LocalDate.now());
            EditMessageText editMessage = new EditMessageText(chatId, msgId,
                    getPreparedText(segments[0], startDate, endDate))
                            .replyMarkup(keyboard).parseMode(ParseMode.MarkdownV2);
            return List.of(answer(callback.id()), editMessage);
        } else if (segments.length == 2) {
            LocalDate startDate = getDateFromCallback(segments[1].split("=")[1]);
            LocalDate endDate = startDate.plusDays(7);
            InlineKeyboardMarkup keyboard = getControlKeyboard(segments[0], startDate);
            EditMessageText editMessage = new EditMessageText(chatId, msgId,
                    getPreparedText(segments[0], startDate, endDate))
                            .replyMarkup(keyboard).parseMode(ParseMode.MarkdownV2);
            return List.of(answer(callback.id()), editMessage);
        }
        return List.of(answer(callback.id()).text(
                "Что-то пошло не так, повторите попытку через некоторое время, либо напишите в @maipomogator_chat"));
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> processInline(CallbackQuery callback,
            String inlineMessageId) {
        String[] segments = callback.data().split(";");
        if (segments.length == 1) {
            LocalDate startDate = getDateFromCallback("current");
            LocalDate endDate = startDate.plusDays(7);
            InlineKeyboardMarkup keyboard = getControlKeyboard(segments[0], LocalDate.now());
            EditMessageText editMessage = new EditMessageText(inlineMessageId,
                    getPreparedText(segments[0], startDate, endDate))
                            .replyMarkup(keyboard).parseMode(ParseMode.MarkdownV2);
            return List.of(answer(callback.id()), editMessage);
        } else if (segments.length == 2) {
            LocalDate startDate = getDateFromCallback(segments[1].split("=")[1]);
            LocalDate endDate = startDate.plusDays(7);
            InlineKeyboardMarkup keyboard = getControlKeyboard(segments[0], startDate);
            EditMessageText editMessage = new EditMessageText(inlineMessageId,
                    getPreparedText(segments[0], startDate, endDate))
                            .replyMarkup(keyboard).parseMode(ParseMode.MarkdownV2);
            return List.of(answer(callback.id()), editMessage);
        }
        return List.of(answer(callback.id()).text(
                "Что-то пошло не так, повторите попытку через некоторое время, либо напишите в @maipomogator_chat"));
    }

    private InlineKeyboardMarkup getControlKeyboard(String prefix, LocalDate date) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton back = new InlineKeyboardButton("⏪")
                .callbackData(prefix + ";from=" + date.minusDays(7).format(CALLBACK_DATE_FORMATTER));
        InlineKeyboardButton today = new InlineKeyboardButton("Текущая")
                .callbackData(prefix + ";from=current");
        InlineKeyboardButton fwd = new InlineKeyboardButton("⏩")
                .callbackData(prefix + ";from=" + date.plusDays(7).format(CALLBACK_DATE_FORMATTER));
        keyboard.addRow(back, today, fwd);
        keyboard.addRow(new InlineKeyboardButton("По дням").callbackData(prefix + ";date=today"));
        keyboard.addRow(new InlineKeyboardButton("Экзамены").callbackData(prefix + ";exams"));
        return keyboard;
    }

    private String getPreparedText(String prefix, LocalDate startDate, LocalDate endDate) {
        String text = builder.getMessageText(prefix, startDate, endDate);
        return escapeForMarkdownV2(text);
    }

    private LocalDate getDateFromCallback(String data) {
        if (data.equals("current")) {
            // почему-то в предыдущей версии использовалась такая конструкция 👇, оставлю её на всякий случай
            // .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            return LocalDate.now().with(DayOfWeek.MONDAY);
        } else {
            return LocalDate.parse(data, CALLBACK_DATE_FORMATTER);
        }
    }
}
