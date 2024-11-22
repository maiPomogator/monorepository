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

import ru.maipomogator.bot.clients.ProfessorRestClient;
import ru.maipomogator.bot.model.Professor;

@Component
public class ProfessorInlineProcessor extends AbstractInlineProcessor {

    private final ProfessorRestClient professorRestClient;

    protected ProfessorInlineProcessor(ProfessorRestClient professorRestClient) {
        super("^(?:[а-яА-ЯёЁ]{2,15} ?){1,5}$");
        this.professorRestClient = professorRestClient;
    }

    @Override
    public Collection<BotApiMethod<? extends Serializable>> process(InlineQuery query) {
        String request = query.getQuery();
        List<Professor> professors = professorRestClient.findByFio(request);
        if (professors == null || professors.isEmpty()) {
            return List.of(AnswerInlineQuery.builder()
                    .inlineQueryId(query.getId())
                    .result(
                            InlineQueryResultArticle.builder()
                                    .id("noProfessors")
                                    .title("Преподаватель не найден")
                                    .description("По запросу %s преподаватель не найден".formatted(request))
                                    .build())
                    .build());
        }

        List<InlineQueryResult> results = new ArrayList<>(professors.size());
        for (Professor professor : professors) {
            String prefix = "prf=" + professor.id();
            InlineKeyboardMarkup keyboard = getKeyboard(prefix);
            String text = escapeForMarkdownV2("*__" + professor.fio() + "__*");
            InputTextMessageContent message = InputTextMessageContent.builder()
                    .messageText(text)
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build();
            results.add(InlineQueryResultArticle.builder()
                    .id(prefix)
                    .title(professor.fio())
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
