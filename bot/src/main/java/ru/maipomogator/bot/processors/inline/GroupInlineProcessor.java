package ru.maipomogator.bot.processors.inline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

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
    public Collection<BotApiMethod<? extends Serializable>> process(InlineQuery query) {
        log.info("inlining group");
        List<Group> groups = groupRestClient.findByName(query.getQuery());
        if (groups == null || groups.isEmpty()) {
            return List.of(AnswerInlineQuery.builder()
                    .inlineQueryId(query.getId()).result(
                            InlineQueryResultArticle.builder()
                                    .id("noGroups")
                                    .title("Группа не найдена")
                                    .description("По запросу %s группа не найдена".formatted(query.getQuery()))
                                    .build())
                    .cacheTime(1)
                    .build());
        }

        List<InlineQueryResult> results = new ArrayList<>(groups.size());
        for (Group group : groups) {
            String prefix = "grp=" + group.id();
            InlineKeyboardMarkup keyboard = getKeyboard(prefix);
            String text = escapeForMarkdownV2("*__Группа " + group.name() + "__*");
            InputTextMessageContent message = InputTextMessageContent.builder()
                    .messageText(text)
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build();
            results.add(InlineQueryResultArticle.builder()
                    .id(prefix)
                    .title(group.name())
                    .inputMessageContent(message)
                    .replyMarkup(keyboard)
                    .build());
        }

        return List.of(AnswerInlineQuery.builder()
                .inlineQueryId(query.getId())
                .results(results)
                .cacheTime(1)
                .build());
    }

    private InlineKeyboardMarkup getKeyboard(String prefix) {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(
                        new InlineKeyboardRow(
                                InlineKeyboardButton.builder()
                                        .text("Загрузить расписание")
                                        .callbackData(prefix)
                                        .build()))
                .build();
    }
}
