package ru.maipomogator.bot.processors.callback;

import static java.lang.System.lineSeparator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.BaseResponse;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.bot.clients.GroupRestClient;
import ru.maipomogator.bot.clients.ProfessorRestClient;
import ru.maipomogator.bot.model.Group;
import ru.maipomogator.bot.model.Lesson;
import ru.maipomogator.bot.model.LessonStatus;
import ru.maipomogator.bot.model.LessonType;
import ru.maipomogator.bot.model.Professor;

@Component
@Log4j2
public class TimetableProcessor extends AbstractCallbackProcessor {
    // TODO сделать более продвинутую систему экранирования (и вернуть * в список)
    private static final String CHARS_TO_BE_ESCAPED = "_[]()~`>#+-=|{}.!";
    private static final DateTimeFormatter CALLBACK_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, d MMMM",
            new Locale("ru", "RU"));

    private final GroupRestClient groupRestClient;
    private final ProfessorRestClient professorRestClient;

    protected TimetableProcessor(GroupRestClient groupRestClient, ProfessorRestClient professorRestClient) {
        super("^(?:grp|prf)=\\d{1,4}(?:;date=(?:\\d{6}|today))?$");
        this.groupRestClient = groupRestClient;
        this.professorRestClient = professorRestClient;
    }

    public InlineKeyboardMarkup getControlKeyboard(String prefix, LocalDate curDate, boolean fromInline) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton back = new InlineKeyboardButton("⬅️")
                .callbackData(prefix + ";date=" + curDate.minusDays(1).format(CALLBACK_DATE_FORMATTER));
        InlineKeyboardButton today = new InlineKeyboardButton("Сегодня")
                .callbackData(prefix + ";date=today");
        InlineKeyboardButton fwd = new InlineKeyboardButton("➡️")
                .callbackData(prefix + ";date=" + curDate.plusDays(1).format(CALLBACK_DATE_FORMATTER));
        keyboard.addRow(back, today, fwd);
        keyboard.addRow(new InlineKeyboardButton("Расписание экзаменов").callbackData(prefix + ";exams"));
        if (!fromInline) {
            keyboard.addRow(CancelCallbackProcessor.cancelButton());
        }
        return keyboard;
    }

    public InputTextMessageContent getMessageContent(String prefix, LocalDate targetDate) {
        String text = getPreparedText(prefix, targetDate);
        return new InputTextMessageContent(text).parseMode(ParseMode.MarkdownV2);
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(CallbackQuery callback, Integer msgId,
            Long chatId) {
        String[] segments = callback.data().split(";");
        if (segments.length == 1) {
            InlineKeyboardMarkup keyboard = getControlKeyboard(segments[0], LocalDate.now(), false);
            EditMessageText editMessage = new EditMessageText(chatId, msgId,
                    getPreparedText(segments[0], LocalDate.now()))
                            .replyMarkup(keyboard).parseMode(ParseMode.MarkdownV2);
            return List.of(answer(callback.id()), editMessage);
        } else if (segments.length == 2) {
            LocalDate targetDate = getDateFromCallback(segments[1].split("=")[1]);
            InlineKeyboardMarkup keyboard = getControlKeyboard(segments[0], targetDate, false);
            EditMessageText editMessage = new EditMessageText(chatId, msgId, getPreparedText(segments[0], targetDate))
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
            InlineKeyboardMarkup keyboard = getControlKeyboard(segments[0], LocalDate.now(), true);
            EditMessageText editMessage = new EditMessageText(inlineMessageId,
                    getPreparedText(segments[0], LocalDate.now()))
                            .replyMarkup(keyboard).parseMode(ParseMode.MarkdownV2);
            return List.of(answer(callback.id()), editMessage);
        } else if (segments.length == 2) {
            LocalDate targetDate = getDateFromCallback(segments[1].split("=")[1]);
            InlineKeyboardMarkup keyboard = getControlKeyboard(segments[0], targetDate, true);
            EditMessageText editMessage = new EditMessageText(inlineMessageId, getPreparedText(segments[0], targetDate))
                    .replyMarkup(keyboard).parseMode(ParseMode.MarkdownV2);
            return List.of(answer(callback.id()), editMessage);
        }
        return List.of(answer(callback.id()).text(
                "Что-то пошло не так, повторите попытку через некоторое время, либо напишите в @maipomogator_chat"));
    }

    // TODO сделать более продвинутую систему экранирования (и вернуть * в список)
    private String getPreparedText(String prefix, LocalDate targetDate) {
        String text = getMsgText(prefix, targetDate);
        StringBuilder sb = new StringBuilder(text.length());

        int escaped = 0;
        for (char c : text.toCharArray()) {
            if (CHARS_TO_BE_ESCAPED.indexOf(c) != -1) {
                sb.append("\\");
                escaped++;
            }
            sb.append(c);
        }

        log.info("Escaped {} characters (old length {}, new length {})", escaped, text.length(), sb.length());

        return sb.toString();
    }

    private String getMsgText(String prefix, LocalDate targetDate) {
        String[] parts = prefix.split("=");
        boolean isProfessor = parts[0].equals("prf");
        String entId = parts[1];
        List<Lesson> dayLessons;
        StringBuilder sb = new StringBuilder();
        if (isProfessor) {
            Professor prf = professorRestClient.findById(entId);
            sb.append("*" + prf.getFullName() + "*").append(lineSeparator());
            dayLessons = professorRestClient.getLessonsBetweenDates(entId, targetDate, targetDate)
                    .stream()
                    .filter(l -> l.status().equals(LessonStatus.SAVED)).sorted().toList();
        } else {
            Group grp = groupRestClient.findById(entId);
            sb.append("*Группа " + grp.name() + "*").append(lineSeparator());
            dayLessons = groupRestClient.getLessonsBetweenDates(entId, targetDate, targetDate).stream()
                    .filter(l -> l.status().equals(LessonStatus.SAVED)).sorted().toList();
        }

        sb.append(formatDate(targetDate)).append(lineSeparator()).append(lineSeparator());

        if (dayLessons.isEmpty()) {
            sb.append("Пар нет").append(lineSeparator()).append(lineSeparator());
        } else {
            for (Lesson lesson : dayLessons) {
                sb.append(formatLesson(lesson, isProfessor)).append(lineSeparator());
            }
        }

        return sb.toString();
    }

    private String formatProfessors(Collection<Professor> professors) {
        UUID zeroUuid = new UUID(0, 0);
        professors.removeIf(p -> p.siteId().equals(zeroUuid));
        return professors.stream().map(Professor::getFullName).collect(Collectors.joining(", "))
                + (professors.isEmpty() ? "" : lineSeparator());
    }

    private LocalDate getDateFromCallback(String data) {
        return data.equals("today") ? LocalDate.now() : LocalDate.parse(data, CALLBACK_DATE_FORMATTER);
    }

    private String formatLesson(Lesson l, boolean isProfessor) {
        StringBuilder sb = new StringBuilder();
        sb.append("Пара с ").append(l.timeStart()).append(" до ").append(l.timeEnd()).append(lineSeparator());
        sb.append(formatTypes(l.types())).append(" ").append(l.name()).append(lineSeparator());
        if (isProfessor) {
            sb.append(formatGroups(l.groups()));
        } else {
            sb.append(formatProfessors(l.professors()));
        }
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

    private String formatDate(LocalDate date) {
        String str = date.format(DATE_FORMATTER);
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
