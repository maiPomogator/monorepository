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
import ru.maipomogator.bot.model.Professor;
import ru.maipomogator.bot.processors.callback.CancelCallbackProcessor;

@Component
public class SelectProfessor extends AbstractMessageProcessor {

    private final ProfessorRestClient professorRestClient;

    public SelectProfessor(ProfessorRestClient professorRestClient) {
        super("^(?:[а-яА-ЯёЁ]{2,15} ?){1,5}$");
        this.professorRestClient = professorRestClient;
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(Message msg, Long chatId) {
        String request = msg.text();

        List<Professor> professors = professorRestClient.findByFio(request);
        SendMessage response;
        if (professors == null || professors.isEmpty()) {
            response = new SendMessage(chatId,
                    "По запросу \"%s\" преподаватель не найден. На всякий случай проверьте ввод. Если вы уверены, что всё правильно, напишите в @maipomogator_chat"
                            .formatted(request))
                                    .replyMarkup(new InlineKeyboardMarkup(CancelCallbackProcessor.cancelButton()));
        } else {
            InlineKeyboardMarkup keyboard = getProfessorsKeyboard(professors);
            response = new SendMessage(chatId, "Результаты поиска по запросу \"%s\":".formatted(request))
                    .replyMarkup(keyboard);
        }
        return List.of(new DeleteMessage(chatId, msg.messageId()), response);
    }

    private InlineKeyboardMarkup getProfessorsKeyboard(List<Professor> professors) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        for (Professor professor : professors) {
            keyboard.addRow(new InlineKeyboardButton(professor.fio()).callbackData("prf=" + professor.id()));
        }
        keyboard.addRow(CancelCallbackProcessor.cancelButton());
        return keyboard;
    }
}
