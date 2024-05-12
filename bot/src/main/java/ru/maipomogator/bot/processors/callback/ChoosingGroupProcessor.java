package ru.maipomogator.bot.processors.callback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

import ru.maipomogator.bot.clients.GroupRestClient;
import ru.maipomogator.bot.clients.MaiRestClient;
import ru.maipomogator.bot.model.Group;
import ru.maipomogator.bot.model.MaiInfo;

@Component
public class ChoosingGroupProcessor extends AbstractCallbackProcessor {

    private final MaiRestClient maiRestClient;
    private final GroupRestClient groupRestClient;

    protected ChoosingGroupProcessor(MaiRestClient maiRestClient, GroupRestClient groupRestClient) {
        super("^fac=\\d{1,2}(?:;crs=[1-9])?(?:;back)?$");
        this.maiRestClient = maiRestClient;
        this.groupRestClient = groupRestClient;
    }

    public SendMessage getInstitutesMessage(Long chatId) {
        return new SendMessage(chatId, "Выберите институт").replyMarkup(makeInstitutesKeyboard());
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> process(CallbackQuery callback, Integer msgId,
            Long chatId) {
        String queryId = callback.id();
        String data = callback.data();
        String[] segments = data.split(";");

        if (segments.length == 1) { // только fac=1
            return List.of(getCoursesMessage(chatId, msgId, segments[0]));
        } else if (segments.length == 2) { // fac=1;crs=3 или fac=1;back
            if (segments[1].equals("back")) {
                return List.of(answer(queryId), getInstitutesMessage(chatId, msgId));
            } else {
                return List.of(answer(queryId), getGroupsMessage(chatId, msgId, segments));
            }
        } else if (segments.length == 3) { // fac=1;crs=3;back
            return List.of(answer(queryId), getCoursesMessage(chatId, msgId, segments[0]));
        }
        return List.of(answer(queryId).text("Что-то пошло не так, попробуйте заново."));
    }

    @Override
    protected Collection<BaseRequest<?, ? extends BaseResponse>> processInline(CallbackQuery callback,
            String inlineMessageId) {
        return List.of(answer(callback.id()).text("Данная кнопка пока не работает в inline режиме."));
    }

    private InlineKeyboardMarkup makeInstitutesKeyboard() {
        MaiInfo info = maiRestClient.getMaiInfo();
        Iterator<String> faculties = info.faculties().iterator();

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        while (faculties.hasNext()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int indexInRow = 0; indexInRow < 3; indexInRow++) {
                if (faculties.hasNext()) {
                    String facName = faculties.next();
                    row.add(new InlineKeyboardButton(facName).callbackData("fac=" + facName));
                }
            }
            keyboard.addRow(row.toArray(InlineKeyboardButton[]::new));
        }

        keyboard.addRow(buttons.cancelButton());
        return keyboard;
    }

    private EditMessageText getInstitutesMessage(Long chatId, int messageId) {
        return new EditMessageText(chatId, messageId, "Выберите институт").replyMarkup(makeInstitutesKeyboard());
    }

    private EditMessageText getCoursesMessage(Long chatId, int messageId, String fac) {
        return new EditMessageText(chatId, messageId, "Теперь курс")
                .replyMarkup(getCoursesKeyboard(fac));
    }

    private InlineKeyboardMarkup getCoursesKeyboard(String fac) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        MaiInfo info = maiRestClient.getMaiInfo();
        int numOfCourses = info.numberOfCourses();
        Integer curCourse = 1;
        while (curCourse <= numOfCourses) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int currentButtonInRow = 0; currentButtonInRow < 3; currentButtonInRow++) {
                InlineKeyboardButton button = new InlineKeyboardButton(curCourse.toString())
                        .callbackData(fac + ";crs=" + curCourse);
                row.add(button);
                curCourse++;
            }
            keyboard.addRow(row.toArray(InlineKeyboardButton[]::new));
        }

        keyboard.addRow(buttons.cancelButton(), buttons.backButton(fac));
        return keyboard;
    }

    private EditMessageText getGroupsMessage(Long chatId, int messageId, String[] segments) {
        int fac = Integer.parseInt(segments[0].split("=")[1]);
        int crs = Integer.parseInt(segments[1].split("=")[1]);
        List<Group> groups = groupRestClient.findByCourseAndFaculty(crs, fac);
        String text = groups.isEmpty() ? "Таких групп нет" : "А теперь группу";
        return new EditMessageText(chatId, messageId, text)
                .replyMarkup(getGroupsKeyboard(groups, segments[0] + ";" + segments[1]));
    }

    private InlineKeyboardMarkup getGroupsKeyboard(List<Group> groups, String prefix) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        Collections.sort(groups);
        Iterator<Group> gIterator = groups.iterator();

        while (gIterator.hasNext()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                if (gIterator.hasNext()) {
                    Group group = gIterator.next();
                    row.add(new InlineKeyboardButton(group.name()).callbackData("grp=" + group.id()));
                }
            }
            keyboard.addRow(row.toArray(InlineKeyboardButton[]::new));
        }

        keyboard.addRow(buttons.cancelButton(), buttons.backButton(prefix));
        return keyboard;
    }
}
