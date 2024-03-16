package ru.maipomogator.bot.processors.inline;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.bot.clients.GroupRestClient;
import ru.maipomogator.bot.model.Group;
import ru.maipomogator.bot.processors.callback.TimetableProcessor;

@Log4j2
@Component
public class GroupInlineProcessor extends AbstractInlineProcessor {
    private final GroupRestClient groupRestClient;
    private final TimetableProcessor timetableProcessor;

    public GroupInlineProcessor(GroupRestClient groupRestClient, TimetableProcessor timetableProcessor) {
        super("^[МмТт][\\dИиУу]{1,2}[ОоВвЗз]-\\d{3}[БбМмАаСс][КкВв]?[КкИи]?-\\d{2}$");
        this.groupRestClient = groupRestClient;
        this.timetableProcessor = timetableProcessor;
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
        String prefix = "grp=" + group.id();
        InlineKeyboardMarkup keyboard = timetableProcessor.getControlKeyboard(prefix, LocalDate.now(), true);
        InputTextMessageContent text = timetableProcessor.getMessageContent(prefix, LocalDate.now());

        return List.of(new AnswerInlineQuery(query.id(),
                new InlineQueryResultArticle(group.id() + "", group.name(), text).replyMarkup(keyboard)).cacheTime(1));
    }
}
