package ru.maipomogator.bot.processors.message;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

import ru.maipomogator.bot.clients.ProfessorRestClient;
import ru.maipomogator.bot.model.FioComparator;
import ru.maipomogator.bot.model.Professor;

@Component
public class SelectProfessor extends AbstractMessageProcessor {

    private final ProfessorRestClient professorRestClient;

    public SelectProfessor(ProfessorRestClient professorRestClient) {
        super("^([а-яА-ЯёЁ]{2,15} ?){1,5}$");
        this.professorRestClient = professorRestClient;
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId) {
        List<Professor> professors = professorRestClient.findAll();
        professors.sort(new FioComparator(msg.text()));

        InlineKeyboardMarkup keyboard = getProfessorsKeyboard(professors.subList(0, Math.min(5, professors.size())));
        return List.of(new DeleteMessage(chatId, msg.messageId()),
                new SendMessage(chatId, "Результаты поиска:").replyMarkup(keyboard));
    }

    private InlineKeyboardMarkup getProfessorsKeyboard(List<Professor> professors) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        for (Professor professor : professors) {
            keyboard.addRow(new InlineKeyboardButton(professor.getFullName()).callbackData("prf=" + professor.id()));
        }
        return keyboard;
    }
}
