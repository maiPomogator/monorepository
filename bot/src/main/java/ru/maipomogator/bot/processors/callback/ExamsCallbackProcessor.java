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
public class ExamsCallbackProcessor extends AbstractCallbackProcessor {
    private static final String CHARS_TO_BE_ESCAPED = "_[]()~`>#+-=|{}.!";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMMM",
            new Locale("ru", "RU"));

    private final GroupRestClient groupRestClient;
    private final ProfessorRestClient professorRestClient;

    protected ExamsCallbackProcessor(GroupRestClient groupRestClient, ProfessorRestClient professorRestClient) {
        super("^(?:grp|prf)=\\d{1,4};exams$");
        this.groupRestClient = groupRestClient;
        this.professorRestClient = professorRestClient;
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(CallbackQuery callback, Integer msgId,
            Long chatId) {
        String[] segments = callback.data().split(";");
        InlineKeyboardMarkup keyboard = getKeyboard(segments[0], false);
        EditMessageText editMessage = new EditMessageText(chatId, msgId, getPreparedText(segments[0]))
                .parseMode(ParseMode.MarkdownV2).replyMarkup(keyboard);

        return List.of(answer(callback.id()), editMessage);
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> processInline(CallbackQuery callback,
            String inlineMessageId) {
        String[] segments = callback.data().split(";");
        InlineKeyboardMarkup keyboard = getKeyboard(segments[0], true);

        EditMessageText editMessage = new EditMessageText(inlineMessageId, getPreparedText(segments[0]))
                .parseMode(ParseMode.MarkdownV2).replyMarkup(keyboard);

        return List.of(editMessage);
    }

    private InlineKeyboardMarkup getKeyboard(String prefix, boolean fromInline) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton back = new InlineKeyboardButton("Назад к расписанию занятий").callbackData(prefix + ";date=today");
        keyboard.addRow(back);
        if (!fromInline) {
            keyboard.addRow(CancelCallbackProcessor.cancelButton());
        }
        return keyboard;
    }

    private String getPreparedText(String prefix) {
        String text = getMsgText(prefix);
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

    private String getMsgText(String prefix) {
        String[] parts = prefix.split("=");
        boolean isProfessor = parts[0].equals("prf");
        String entId = parts[1];
        List<Lesson> exams;
        StringBuilder sb = new StringBuilder();
        if (isProfessor) {
            Professor prf = professorRestClient.findById(entId);
            sb.append("*Экзамены*").append(lineSeparator());
            sb.append("*" + prf.getFullName() + "*").append(lineSeparator()).append(lineSeparator());
            exams = professorRestClient
                    .getLessonsBetweenDates(entId, LocalDate.of(2024, 5, 1), LocalDate.of(2024, 7, 1))
                    .stream().filter(l -> l.types().contains(LessonType.EXAM))
                    .filter(l -> l.status().equals(LessonStatus.SAVED)).sorted().toList();
        } else {
            Group grp = groupRestClient.findById(entId);
            sb.append("*Экзамены*").append(lineSeparator());
            sb.append("*Группа " + grp.name() + "*").append(lineSeparator()).append(lineSeparator());
            exams = groupRestClient.getLessonsBetweenDates(entId, LocalDate.of(2024, 5, 1), LocalDate.of(2024, 7, 1))
                    .stream().filter(l -> l.types().contains(LessonType.EXAM))
                    .filter(l -> l.status().equals(LessonStatus.SAVED)).sorted().toList();
        }

        if (exams.isEmpty()) {
            sb.append("Экзаменов вероятно ещё нет (если они отображаются на сайте, напишите в @maipomogator_chat)")
                    .append(lineSeparator()).append(lineSeparator());
        } else {
            for (Lesson lesson : exams) {
                sb.append(formatExam(lesson, isProfessor)).append(lineSeparator());
            }
        }

        return sb.toString();
    }

    private String formatExam(Lesson l, boolean isProfessor) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatDate(l.date())).append(" с ").append(l.timeStart()).append(" до ")
                .append(l.timeEnd())
                .append(lineSeparator());
        sb.append(l.name()).append(lineSeparator());
        if (isProfessor) {
            sb.append(formatGroups(l.groups()));
        } else {
            sb.append(formatProfessors(l.professors()));
        }
        sb.append(String.join(", ", l.rooms())).append(lineSeparator());
        return sb.toString();
    }

    private String formatProfessors(Collection<Professor> professors) {
        UUID zeroUuid = new UUID(0, 0);
        professors.removeIf(p -> p.siteId().equals(zeroUuid));
        return professors.stream().map(Professor::getFullName).collect(Collectors.joining(", "))
                + (professors.isEmpty() ? "" : lineSeparator());
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
