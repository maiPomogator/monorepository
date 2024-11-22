package ru.maipomogator.bot.processors.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import ru.maipomogator.bot.clients.ProfessorRestClient;
import ru.maipomogator.bot.model.Professor;

@Component
public class SelectProfessor extends AbstractMessageProcessor {

    private final ProfessorRestClient professorRestClient;

    public SelectProfessor(ProfessorRestClient professorRestClient) {
        super("^(?:[а-яА-ЯёЁ]{2,15} ?){1,5}$");
        this.professorRestClient = professorRestClient;
    }

    @Override
    protected Collection<BotApiMethod<? extends Serializable>> process(Message msg, String chatId) {
        String request = msg.getText();

        List<Professor> professors = professorRestClient.findByFio(request);
        SendMessage response;
        if (professors == null || professors.isEmpty()) {
            response = SendMessage.builder().chatId(chatId).text(
                    "По запросу \"%s\" преподаватель не найден. На всякий случай проверьте ввод. Если вы уверены, что всё правильно, напишите в @maipomogator_chat"
                            .formatted(request))
                    .replyMarkup(
                            InlineKeyboardMarkup.builder().keyboardRow(new InlineKeyboardRow(buttons.cancelButton()))
                                    .build())
                    .build();
        } else {
            InlineKeyboardMarkup keyboard = getProfessorsKeyboard(professors);
            response = SendMessage.builder().chatId(chatId)
                    .text("Результаты поиска по запросу \"%s\":".formatted(request)).replyMarkup(keyboard).build();
        }
        return List.of(new DeleteMessage(chatId, msg.getMessageId()), response);
    }

    private InlineKeyboardMarkup getProfessorsKeyboard(List<Professor> professors) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>(professors.size() + 1);
        for (Professor professor : professors) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(professor.fio())
                    .callbackData("prf=" + professor.id())
                    .build();
            keyboard.add(new InlineKeyboardRow(button));
        }
        keyboard.add(new InlineKeyboardRow(buttons.cancelButton()));

        return new InlineKeyboardMarkup(keyboard);
    }
}