package ru.maipomogator.bot.dispatchers;

import com.pengrad.telegrambot.model.Update;

public interface UpdateDispatcher {
    void dispatch(Update update);
}
