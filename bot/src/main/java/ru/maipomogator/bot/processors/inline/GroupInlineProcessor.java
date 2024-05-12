package ru.maipomogator.bot.processors.inline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InlineQueryResult;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.bot.clients.GroupRestClient;
import ru.maipomogator.bot.model.Group;

@Log4j2
@Component
public class GroupInlineProcessor extends AbstractInlineProcessor {
    private final GroupRestClient groupRestClient;

    public GroupInlineProcessor(GroupRestClient groupRestClient) {
        super("^(?:[МмТт][\\dИиСсУу]{1,2}[ОоВвЗз]-)?\\d{3}(?:[БбМмАаСс][КкВв]?[КкИи]?)?(?:-\\d{2}$)?$");
        this.groupRestClient = groupRestClient;
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
            InlineKeyboardMarkup keyboard = getKeyboard(prefix);
            String text = escapeForMarkdownV2("*__Группа " + group.name() + "__*");
            InputTextMessageContent message = new InputTextMessageContent(text).parseMode(ParseMode.MarkdownV2);
            results.add(new InlineQueryResultArticle(prefix, group.name(), message).replyMarkup(keyboard));
        }

        return List.of(new AnswerInlineQuery(query.id(), results.toArray(InlineQueryResult[]::new)).cacheTime(1));
    }

    private InlineKeyboardMarkup getKeyboard(String prefix) {
        return new InlineKeyboardMarkup(new InlineKeyboardButton("Загрузить расписание").callbackData(prefix));
    }
}
