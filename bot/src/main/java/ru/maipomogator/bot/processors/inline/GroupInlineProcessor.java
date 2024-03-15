package ru.maipomogator.bot.processors.inline;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.bot.clients.GroupRestClient;
import ru.maipomogator.bot.model.Group;

@Log4j2
@Component
public class GroupInlineProcessor extends AbstractInlineProcessor {
    private static final DateTimeFormatter CALLBACK_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    private final GroupRestClient groupRestClient;

    public GroupInlineProcessor(GroupRestClient groupRestClient) {
        super("^[МмТт][\\dИиУу]{1,2}[ОоВвЗз]-\\d{3}[БбМмАаСс][КкВв]?[КкИи]?-\\d{2}$");
        this.groupRestClient = groupRestClient;
    }

    @Override
    public Collection<? extends BaseRequest<?, ? extends BaseResponse>> process(InlineQuery query) {
        log.info("inlining group");
        Group group = groupRestClient.findByName(query.query());
        if (group == null) {
            return List.of(new AnswerInlineQuery(query.id(),
                    new InlineQueryResultArticle("nogroup", "Группа не найдена",
                            "Группа %s не найдена".formatted(query.query()))).cacheTime(1));
        }
        LocalDate curDate = LocalDate.now();
        String grp = "grp=" + group.id();
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton back = new InlineKeyboardButton("⬅️")
                .callbackData(grp + ";date=" + curDate.minusDays(1).format(CALLBACK_DATE_FORMATTER));
        InlineKeyboardButton today = new InlineKeyboardButton("Сегодня")
                .callbackData(grp + ";date=today");
        InlineKeyboardButton fwd = new InlineKeyboardButton("➡️")
                .callbackData(grp + ";date=" + curDate.plusDays(1).format(CALLBACK_DATE_FORMATTER));
        keyboard.addRow(back, today, fwd);

        return List.of(new AnswerInlineQuery(query.id(),
                new InlineQueryResultArticle(group.id() + "", group.name(), "\"Выбрана\" группа " + group.name())
                        .replyMarkup(keyboard)));
    }
}
