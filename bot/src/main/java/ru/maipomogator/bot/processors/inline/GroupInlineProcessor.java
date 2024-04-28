package ru.maipomogator.bot.processors.inline;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InlineQueryResult;
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
        super("^(?:[МмТт][\\dИиСсУу]{1,2}[ОоВвЗз]-)?\\d{3}(?:[БбМмАаСс][КкВв]?[КкИи]?)?(?:-\\d{2}$)?$");
        this.groupRestClient = groupRestClient;
        this.timetableProcessor = timetableProcessor;
    }

    @Override
    public Collection<? extends BaseRequest<?, ? extends BaseResponse>> process(InlineQuery query) {
        log.info("inlining group");
        List<Group> groups = groupRestClient.findByName(query.query());
        if (groups == null || groups.isEmpty()) {
            return List.of(new AnswerInlineQuery(query.id(),
                    new InlineQueryResultArticle("noGroups", "Группа не найдена",
                            "По запросу %s группа не найдена".formatted(query.query()))).cacheTime(1));
        }

        List<InlineQueryResult<?>> results = new ArrayList<>(groups.size());
        for (Group group : groups) {
            String prefix = "grp=" + group.id();
            InlineKeyboardMarkup keyboard = timetableProcessor.getControlKeyboard(prefix, LocalDate.now());
            InputTextMessageContent text = timetableProcessor.getMessageContent(prefix, LocalDate.now());
            results.add(new InlineQueryResultArticle(prefix, group.name(), text).replyMarkup(keyboard));
        }

        return List.of(new AnswerInlineQuery(query.id(), results.toArray(InlineQueryResult[]::new)).cacheTime(1));
    }
}
