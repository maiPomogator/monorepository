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
    public Collection<? extends BaseRequest<?, ? extends BaseResponse>> process(InlineQuery query) {
        String request = query.query();
        List<Professor> professors = professorRestClient.findByFio(request);
        if (professors == null || professors.isEmpty()) {
            return List.of(new AnswerInlineQuery(query.id(),
                    new InlineQueryResultArticle("noProfessors", "Преподаватель не найден",
                            "По запросу %s преподаватель не найден".formatted(request))).cacheTime(1));
        }

        List<InlineQueryResult<?>> results = new ArrayList<>(professors.size());
        for (Professor professor : professors) {
            String prefix = "prf=" + professor.id();
            InlineKeyboardMarkup keyboard = getKeyboard(prefix);
            String text = escapeForMarkdownV2("*__" + professor.fio() + "__*");
            InputTextMessageContent message = new InputTextMessageContent(text)
                    .parseMode(ParseMode.MarkdownV2);
            results.add(new InlineQueryResultArticle(prefix, professor.fio(), message).replyMarkup(keyboard));
        }

        return List.of(new AnswerInlineQuery(query.id(), results.toArray(InlineQueryResult[]::new)).cacheTime(1));
    }

    private InlineKeyboardMarkup getKeyboard(String prefix) {
        return new InlineKeyboardMarkup(new InlineKeyboardButton("Загрузить расписание").callbackData(prefix));
    }
}
