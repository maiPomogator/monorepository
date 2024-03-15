package ru.maipomogator.bot.processors.callback;

import static java.lang.System.lineSeparator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.BaseResponse;

import ru.maipomogator.bot.clients.ProfessorRestClient;
import ru.maipomogator.bot.model.Group;
import ru.maipomogator.bot.model.Lesson;
import ru.maipomogator.bot.model.LessonStatus;
import ru.maipomogator.bot.model.LessonType;

@Component
public class ProfessorTimetableProcessor extends AbstractCallbackProcessor {
    private static final DateTimeFormatter CALLBACK_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, d MMMM",
            new Locale("ru", "RU"));

    private final ProfessorRestClient professorRestClient;

    public ProfessorTimetableProcessor(ProfessorRestClient professorRestClient) {
        super("^prf=\\d{1,4}(;date=(\\d{6}|today))?$");
        this.professorRestClient = professorRestClient;
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(CallbackQuery callback, Integer msgId, Long chatId) {
        String[] segments = callback.data().split(";");
        String groupId = segments[0].split("=")[1];
        if (segments.length == 1) {
            EditMessageText editMessage = getDayMsg(chatId, msgId, segments[0], groupId, LocalDate.now());
            return List.of(answer(callback.id()), editMessage);
        } else if (segments.length == 2) {
            LocalDate targetDate = getDateFromCallback(segments[1].split("=")[1]);
            EditMessageText editMessageText = getDayMsg(chatId, msgId, segments[0], groupId, targetDate);
            return List.of(answer(callback.id()), editMessageText);
        }

        return List.of(answer(callback.id()));
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> processInline(CallbackQuery callback,
            String inlineMessageId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processInline'");
    }

    private LocalDate getDateFromCallback(String data) {
        return data.equals("today") ? LocalDate.now() : LocalDate.parse(data, CALLBACK_DATE_FORMATTER);
    }

    private InlineKeyboardMarkup getControlKeyboard(String prf, LocalDate curDate) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton back = new InlineKeyboardButton("⬅️")
                .callbackData(prf + ";date=" + curDate.minusDays(1).format(CALLBACK_DATE_FORMATTER));
        InlineKeyboardButton today = new InlineKeyboardButton("Сегодня")
                .callbackData(prf + ";date=today");
        InlineKeyboardButton fwd = new InlineKeyboardButton("➡️")
                .callbackData(prf + ";date=" + curDate.plusDays(1).format(CALLBACK_DATE_FORMATTER));
        keyboard.addRow(back, today, fwd);
        keyboard.addRow(CancelCallbackProcessor.cancelButton());

        return keyboard;
    }

    private EditMessageText getDayMsg(Long chatId, int messageId, String prf, String groupId, LocalDate targetDate) {
        List<Lesson> dayLessons = professorRestClient.getLessonsBetweenDates(groupId, targetDate, targetDate).stream()
                .filter(l -> l.status().equals(LessonStatus.SAVED)).sorted(Comparator.comparing(Lesson::timeStart))
                .toList();
        StringBuilder sb = new StringBuilder();
        sb.append(getFormattedDate(targetDate)).append(lineSeparator()).append(lineSeparator());

        if (dayLessons.isEmpty()) {
            sb.append("Пар нет").append(lineSeparator()).append(lineSeparator());
        } else {
            for (Lesson lesson : dayLessons) {
                sb.append(getFormattedLesson(lesson)).append(lineSeparator());
            }
        }

        return new EditMessageText(chatId, messageId, sb.toString()).replyMarkup(getControlKeyboard(prf, targetDate));
    }

    private String getFormattedDate(LocalDate date) {
        String str = date.format(DATE_FORMATTER);
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private String getFormattedLesson(Lesson l) {
        StringBuilder sb = new StringBuilder();
        sb.append("Пара с ").append(l.timeStart()).append(" до ").append(l.timeEnd()).append(lineSeparator());
        sb.append(formatTypes(l.types())).append(" ").append(l.name()).append(lineSeparator());
        sb.append(formatGroups(l.groups()));
        sb.append(String.join(", ", l.rooms())).append(lineSeparator());
        return sb.toString();
    }

    private String formatTypes(List<LessonType> types) {
        return String.join(",", types.stream().map(LessonType::getShortName).toList());
    }

    private String formatGroups(Collection<Group> professors) {
        return professors.stream().map(Group::name).collect(Collectors.joining(", "))
                + (professors.isEmpty() ? "" : lineSeparator());
    }
}