package ru.maipomogator.bot.processors.callback;

import static java.lang.System.lineSeparator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.BaseResponse;

import ru.maipomogator.bot.clients.GroupRestClient;
import ru.maipomogator.bot.clients.ProfessorRestClient;
import ru.maipomogator.bot.model.Group;
import ru.maipomogator.bot.model.Lesson;
import ru.maipomogator.bot.model.Professor;
import ru.maipomogator.bot.service.TimetableService;

@Component
public class ExamsCallbackProcessor extends AbstractCallbackProcessor {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMMM",
            Locale.of("ru"));

    private final GroupRestClient groupRestClient;
    private final ProfessorRestClient professorRestClient;

    private final TimetableService timetableService;

    protected ExamsCallbackProcessor(GroupRestClient groupRestClient, ProfessorRestClient professorRestClient,
            TimetableService timetableService) {
        super("^(?:grp|prf)=\\d{1,4};exams$");
        this.groupRestClient = groupRestClient;
        this.professorRestClient = professorRestClient;
        this.timetableService = timetableService;
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(CallbackQuery callback, Integer msgId,
            Long chatId) {
        String[] segments = callback.data().split(";");
        InlineKeyboardMarkup keyboard = getKeyboard(segments[0]);
        EditMessageText editMessage = new EditMessageText(chatId, msgId, getPreparedText(segments[0]))
                .parseMode(ParseMode.MarkdownV2).replyMarkup(keyboard);

        return List.of(answer(callback.id()), editMessage);
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> processInline(CallbackQuery callback,
            String inlineMessageId) {
        String[] segments = callback.data().split(";");
        InlineKeyboardMarkup keyboard = getKeyboard(segments[0]);

        EditMessageText editMessage = new EditMessageText(inlineMessageId, getPreparedText(segments[0]))
                .parseMode(ParseMode.MarkdownV2).replyMarkup(keyboard);

        return List.of(editMessage);
    }

    private InlineKeyboardMarkup getKeyboard(String prefix) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton back = new InlineKeyboardButton("Расписание занятий")
                .callbackData(prefix + ";date=today");
        keyboard.addRow(back);
        return keyboard;
    }

    private String getPreparedText(String prefix) {
        String text = getMsgText(prefix);
        return escapeForMarkdownV2(text);
    }

    private String getMsgText(String prefix) {
        String[] parts = prefix.split("=");
        boolean isProfessor = parts[0].equals("prf");
        Long entId = Long.parseLong(parts[1]);
        List<Lesson> exams;
        StringBuilder sb = new StringBuilder();
        if (isProfessor) {
            Professor professor = professorRestClient.findById(entId);
            sb.append("*Экзамены*").append(lineSeparator());
            sb.append("*" + professor.fio() + "*").append(lineSeparator()).append(lineSeparator());
            exams = timetableService.getExamsForProfessor(entId);
        } else {
            Group group = groupRestClient.findById(entId);
            sb.append("*Экзамены*").append(lineSeparator());
            sb.append("*Группа " + group.name() + "*").append(lineSeparator()).append(lineSeparator());
            exams = timetableService.getExamsForGroup(entId);
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
        return professors.stream().map(Professor::fio).collect(Collectors.joining(", "))
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
