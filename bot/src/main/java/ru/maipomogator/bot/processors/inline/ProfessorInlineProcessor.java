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

import ru.maipomogator.bot.clients.ProfessorRestClient;
import ru.maipomogator.bot.model.FioComparator;
import ru.maipomogator.bot.model.Professor;
import ru.maipomogator.bot.processors.callback.TimetableProcessor;

@Component
public class ProfessorInlineProcessor extends AbstractInlineProcessor {

    private final ProfessorRestClient professorRestClient;
    private final TimetableProcessor timetableProcessor;

    protected ProfessorInlineProcessor(ProfessorRestClient professorRestClient, TimetableProcessor timetableProcessor) {
        super("^(?:[а-яА-ЯёЁ]{2,15} ?){1,5}$");
        this.professorRestClient = professorRestClient;
        this.timetableProcessor = timetableProcessor;
    }

    @Override
    public Collection<? extends BaseRequest<?, ? extends BaseResponse>> process(InlineQuery query) {
        List<Professor> professors = professorRestClient.findAll();
        professors.sort(new FioComparator(query.query()));

        List<InlineQueryResult<?>> results = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Professor prf = professors.get(i);
            String prefix = "prf=" + prf.id();
            InlineKeyboardMarkup keyboard = timetableProcessor.getControlKeyboard(prefix, LocalDate.now());
            InputTextMessageContent text = timetableProcessor.getMessageContent(prefix, LocalDate.now());
            results.add(
                    new InlineQueryResultArticle(prefix, prf.getFullName(), text).replyMarkup(keyboard));
        }

        return List.of(new AnswerInlineQuery(query.id(), results.toArray(InlineQueryResult[]::new)).cacheTime(1));
    }
}
